package com.diary.domain.diary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMember.Role;
import com.diary.domain.member.entity.DiaryMemberId;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.global.auth.CustomUserDetails;
import com.diary.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;

    // 다이어리 생성 서비스 : 예외처리 완료
    public DiaryResponse createDiary(Long memberId, DiaryCreateRequest requestDto) {
        // 예외처리 : 이름 공백
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new CustomException("일기장 이름은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Diary diary = Diary.builder()
                .name(requestDto.getName())
                .owner(owner)
                .isDeleted(false)
                .build();

        diaryRepository.save(diary);

        // OWNER로 DiaryMember 등록
        DiaryMember diaryMember = DiaryMember.builder()
                .id(new DiaryMemberId(diary.getId(), owner.getId()))
                .diary(diary)
                .member(owner)
                .role(Role.valueOf("OWNER"))
                .accepted(true)
                .build();

        diaryMemberRepository.save(diaryMember);

        return DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .ownerId(diary.getOwner().getId())
                .build();
    }

    // 다이어리 목록 조회 : 소유권 없이도 전체 목록 조회
    @Transactional(readOnly = true)
    public List<DiaryResponse> getDiaryList(Long memberId) {

        return diaryRepository.findAllByOwnerIdAndIsDeletedFalse(memberId)
                .stream()
                .map(diary -> DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .ownerId(diary.getOwner().getId())
                .build())
                .collect(Collectors.toList());
    }

    public List<Diary> findAllByMember(Long memberId) {
        return diaryRepository.findByOwnerIdAndIsDeletedFalse(memberId);
    }

    // 자신이 참여 중인 일기장 전체 조회 (OWNER + GUEST + accepted = true)
    public List<DiaryResponse> getMyDiaries(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<DiaryMember> diaryMembers = diaryMemberRepository.findByMemberAndAcceptedTrue(member);

        return diaryMembers.stream()
                .map(dm -> {
                    Diary diary = dm.getDiary();
                    return DiaryResponse.from(diary, dm);
                })
                .collect(Collectors.toList());
    }

    // 다이어리 단일 조회 : 404 예외처리 완료
    @Transactional(readOnly = true)
    public DiaryResponse getDiary(Long diaryId, Long memberId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND)); // 404

        return DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .ownerId(diary.getOwner().getId())
                .build();
    }

    // 다이어리 수정 요청 : 권한 403 예외처리 완료
    @Transactional
    public DiaryResponse updateDiary(Long diaryId, Long memberId, DiaryUpdateRequest requestDto) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        // 권한 체크 - 소속 여부만 확인
        diaryMemberRepository.findByDiaryIdAndMemberId(diaryId, userId)
                .orElseThrow(() -> new CustomException("권한 없음.", HttpStatus.FORBIDDEN));

        // 직접 수정
        diary.setName(requestDto.getName());

        // save()는 필요 없음 (영속성 컨텍스트)
        return DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .ownerId(diary.getOwner().getId())
                .build();
    }

    // 다이어리 삭제 요청 :
    @Transactional
    public void deleteDiary(Long diaryId) {
        // 1) 다이어리 조회 + 404 예외
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // 2) 현재 로그인한 유저 ID 꺼내기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        // 3) Diary_Member에서 소속 확인 (Role 체크는 나중)
        diaryMemberRepository.findByDiaryIdAndMemberId(diaryId, userId)
                .orElseThrow(() -> new CustomException("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN));

        // 4) 연관 엔트리 orphanRemoval → clear()
        if (diary.getEntries() != null && !diary.getEntries().isEmpty()) {
            diary.getEntries().clear();
        }

        // 5) Soft Delete
        diary.setIsDeleted(true);

        // 변경감지로 save() 생략 가능
        diaryRepository.save(diary);
    }
}
