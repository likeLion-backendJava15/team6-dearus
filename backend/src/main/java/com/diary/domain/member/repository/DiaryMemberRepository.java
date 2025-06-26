package com.diary.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMemberId;

public interface DiaryMemberRepository extends JpaRepository<DiaryMember, DiaryMemberId> {
    // 해당 유저가 소속된 모든 다이어리 조회
    List<DiaryMember> findByMemberId(Long memberId);

    // 특정 유저가 특정 다이어리에 참여 중인지 확인
    Optional<DiaryMember> findByDiaryIdAndMemberId(Long diaryId, Long memberId);
}
