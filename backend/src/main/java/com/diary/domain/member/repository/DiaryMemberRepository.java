package com.diary.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMemberId;
import com.diary.domain.member.entity.Member;

public interface DiaryMemberRepository extends JpaRepository<DiaryMember, DiaryMemberId> {

    /**
     * 특정 다이어리에 이미 해당 멤버가 포함되어 있는지 확인 (초대 중복 방지용)
     */
    boolean existsByDiaryAndMember(Diary diary, Member member);

    /**
     * Diary, Member 엔티티를 기준으로 DiaryMember 조회 (초대 수락, 권한 확인 등에 사용)
     */
    Optional<DiaryMember> findByDiaryAndMember(Diary diary, Member member);

    /**
     * 다이어리 ID와 유저 ID로 DiaryMember 조회 (권한 확인 또는 추방 기능 등에 사용)
     */
    Optional<DiaryMember> findByDiaryIdAndMemberId(Long diaryId, Long memberId);

    /**
     * 해당 다이어리에 수락(accepted=true)한 멤버들만 조회 (멤버 목록 API에서 사용)
     */
    List<DiaryMember> findByDiaryIdAndAcceptedTrue(Long diaryId);

    List<DiaryMember> findAllByMember(Member member);

    List<DiaryMember> findByMemberAndAcceptedTrue(Member member);

    List<DiaryMember> findByMemberAndAcceptedFalse(Member member);
}
