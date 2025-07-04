package com.diary.domain.member.service;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.member.dto.InviteRequest;
import com.diary.domain.member.dto.InviteResponse;
import com.diary.domain.member.dto.MemberResponse;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMember.Role;
import com.diary.domain.member.entity.DiaryMemberId;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.global.auth.CustomUserDetails;
import com.diary.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberInviteService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;

    /**
     * 1. 멤버 초대 *
     */
    public void inviteMember(Long diaryId, InviteRequest requestDto) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new CustomException("해당 이메일을 가진 유저가 없습니다.", HttpStatus.NOT_FOUND));

        if (diaryMemberRepository.existsByDiaryAndMember(diary, member)) {
            throw new CustomException("이미 초대된 사용자입니다.", HttpStatus.CONFLICT);
        }

        DiaryMember diaryMember = DiaryMember.builder()
                .id(new DiaryMemberId(diaryId, member.getId()))
                .diary(diary)
                .member(member)
                .role(Role.GUEST)
                .accepted(false)
                .build();

        diaryMemberRepository.save(diaryMember);
    }

    /**
     * 2. 초대 수락 *
     */
    public void acceptInvite(Long diaryId) {
        Long currentUserId = getCurrentUserId();

        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        DiaryMember diaryMember = diaryMemberRepository.findByDiaryAndMember(diary, member)
                .orElseThrow(() -> new CustomException("초대 기록이 없습니다.", HttpStatus.NOT_FOUND));

        if (diaryMember.isAccepted()) {
            throw new CustomException("이미 수락된 초대입니다.", HttpStatus.CONFLICT);
        }

        diaryMember.setAccepted(true);
        diaryMemberRepository.save(diaryMember);
    }

    /**
     * 3. 수락된 멤버 목록 조회 *
     */
    public List<MemberResponse> getAcceptedMembers(Long diaryId) {
        if (!diaryRepository.existsById(diaryId)) {
            throw new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        return diaryMemberRepository.findByDiaryIdAndAcceptedTrue(diaryId).stream()
                .map(dm -> new MemberResponse(
                dm.getMember().getId(),
                dm.getMember().getNickname(),
                dm.getRole()
        ))
                .collect(Collectors.toList());
    }

    /**
     * 4. 멤버 추방 *
     */
    public void removeMember(Long diaryId, Long userId) {
        DiaryMember diaryMember = diaryMemberRepository.findByDiaryIdAndMemberId(diaryId, userId)
                .orElseThrow(() -> new CustomException("멤버가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        diaryMemberRepository.delete(diaryMember);
    }

    // 초대 확인
    public List<InviteResponse> getMyInvites() {
        Member currentUser = getCurrentUser();

        List<DiaryMember> pendingInvites = diaryMemberRepository.findByMemberAndAcceptedFalse(currentUser);

        return pendingInvites.stream()
                .map(dm -> {
                    Diary diary = dm.getDiary();
                    Member inviter = diary.getOwner();

                    return new InviteResponse(
                            diary.getId(),
                            diary.getName(),
                            inviter.getNickname() != null ? inviter.getNickname() : inviter.getUserId()
                    );
                })
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }

    private Member getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    // 초대 거절
    @Transactional
    public void declineInvite(Long diaryId, Long memberId) {
        DiaryMember diaryMember = diaryMemberRepository.findByDiaryIdAndMemberId(diaryId, memberId)
                .orElseThrow(() -> new CustomException("초대 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (diaryMember.isAccepted()) {
            throw new CustomException("이미 수락한 초대는 거절할 수 없습니다.", HttpStatus.CONFLICT);
        }

        diaryMemberRepository.delete(diaryMember);  // 아직 수락되지 않은 경우만 삭제
    }

}
