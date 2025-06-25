package com.diary.domain.emotion.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diary.domain.emotion.entity.EmotionStat;

@Repository
public interface EmotionStatRepository extends JpaRepository<EmotionStat, Long> {
    // 전체 감정 통계
    @Query("""
                SELECT e.emotion AS emotion, COUNT(e) AS count
                FROM DiaryEntry e
                WHERE e.diary.id = :diaryId
                  AND (:start IS NULL OR e.createdAt >= :start)
                  AND (:end IS NULL OR e.createdAt <= :end)
                GROUP BY e.emotion
            """)
    List<EmotionCountProjection> findTotalEmotionCounts(
            @Param("diaryId") Long diaryId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
