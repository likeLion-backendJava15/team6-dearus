package com.diary.domain.diary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMember.Role;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.domain.member.service.MemberService;
import com.diary.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  // 생성자 의존성 주입 애너테이션
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberService memberService;

    // 일기장 생성 구현 <완료>
    public DiaryResponse createDiary(DiaryCreateRequest dto) {
        //DiaryMember currentMember = memberService.getCurrentUser();   //(멤버 유저 엔티티 가정)

        
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {      // 이름이 null이거나 공백일 경우 예외
            throw new CustomException("일기장 이름은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        Diary diary = Diary.builder()
                .name(dto.getName())
                .build();

        Diary saved = diaryRepository.save(diary);
        
        //memberService.addMemberToDiary(currentMember, saved, Role.OWNER);  //(현재 유저를 Owner로 멤버 추가 가정)
        
        return toResponseDTO(saved);
    }

    // 유저 기준 일기장 - <멤버 검증 고려 X>
    public DiaryResponse getDiary(Long id) {
        Diary diary = diaryRepository.findById(id)
                    .filter(d -> !d.isDeleted())
                    .orElseThrow(() -> new CustomException("존재하지 않거나 삭제된 일기장입니다.", HttpStatus.NOT_FOUND));
        return toResponseDTO(diary);
    }

    // 유저 기준 일기장 목록 조회 구현 - <멤버 검증 고려 X>
    public List<DiaryResponse> getDiaryList() {
        List<DiaryResponse> diaries = diaryRepository.findAll()
                            .stream()
                            .filter(d -> !d.isDeleted())  // 소프트 삭제된 건 제외
                            .map(this::toResponseDTO)
                            .collect(Collectors.toList());
        return diaries;
    }

    // 일기장 수정 구현 (허용되지 않은 사용자 : 403 보류)
    public void updateDiary(Long id, DiaryUpdateRequest dto) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new CustomException("대상 일기장이 없습니다.", HttpStatus.NOT_FOUND));

        // 일기장 사용자 검증 (보류)
        
        diary.setName(dto.getName());
        diaryRepository.save(diary);
    }

    // 일기장 삭제 구현
    public void deleteDiary(Long diaryId, Long memberId) {
        Diary diary = diaryRepository.findById(diaryId)
                    .orElseThrow(() -> new CustomException("일기장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        diary.setDeleted(true); // 권한 확인 or 멤버 확인은 생략하고 deleted 마킹
        diaryRepository.save(diary);
    }

    // 일기장 응답 빌더
    private DiaryResponse toResponseDTO(Diary diary) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .build();
    }
}
