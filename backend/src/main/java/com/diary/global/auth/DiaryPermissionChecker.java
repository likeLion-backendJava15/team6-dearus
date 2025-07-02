package com.diary.global.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMemberId;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiaryPermissionChecker {

    private final DiaryMemberRepository diaryMemberRepository;

    public void checkAccess(Long userId, Long diaryId) {
        DiaryMemberId id = new DiaryMemberId(diaryId, userId);
        DiaryMember mem = diaryMemberRepository.findById(id).orElseThrow(() -> new CustomException("일기장 접근 권한 없음", HttpStatus.FORBIDDEN));

        if (!mem.isAccepted()) {
            throw new CustomException("초대를 수락하지 않은 사용자입니다.", HttpStatus.FORBIDDEN);
        }

    }

    public boolean isOwner(Long userId, Long diaryId) {
        return diaryMemberRepository.findById(new DiaryMemberId(diaryId, userId))
                .map(mem -> mem.isAccepted() && mem.getRole() == DiaryMember.Role.OWNER)
                .orElse(false);
    }
}
