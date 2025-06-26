package com.diary.domain.emotion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.AbstractMap;

import org.springframework.stereotype.Repository;

import com.diary.domain.emotion.dto.EmotionCountDTO;
import com.diary.domain.emotion.dto.EmotionStatResponse;
import com.diary.domain.emotion.dto.UserEmotionCountDTO;
import com.diary.domain.entry.entity.QDiaryEntry;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmotionStatRepositoryImpl implements EmotionStatRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public EmotionStatResponse getEmotionStatResponse(Long diaryId, LocalDateTime start, LocalDateTime end) {
    QDiaryEntry diaryEntry = QDiaryEntry.diaryEntry;

    // 전체 감정 통계
    List<EmotionCountDTO> totalEmotionCounts = queryFactory.select(diaryEntry.emotion.stringValue(), diaryEntry.count())
        .from(diaryEntry)
        .where(diaryEntry.diary.id.eq(diaryId).and(start != null ? diaryEntry.createdAt.goe(start) : null)
            .and(end != null ? diaryEntry.createdAt.loe(end) : null))
        .groupBy(diaryEntry.emotion).fetch().stream()
        // DTO로 변환 
        .map(tuple -> new EmotionCountDTO(tuple.get(0, String.class), tuple.get(1, Long.class)))
        .collect(Collectors.toList());

    // 유저별 감정 통계
    List<UserEmotionCountDTO> userEmotionCounts = queryFactory
        .select(diaryEntry.author.id, diaryEntry.author.nickname, diaryEntry.emotion.stringValue(), diaryEntry.count())
        .from(diaryEntry).where(diaryEntry.diary.id.eq(diaryId)
            .and(start != null ? diaryEntry.createdAt.goe(start) : null)
            .and(end != null ? diaryEntry.createdAt.loe(end) : null))
        .groupBy(diaryEntry.author.id, diaryEntry.author.nickname, diaryEntry.emotion)
        .fetch().stream()
        // {(userid, nickname) : ["행복", 3]} 형태로 재배치
        .collect(Collectors.groupingBy(tuple -> new AbstractMap.SimpleEntry<>(tuple.get(0, Long.class),
            tuple.get(1, String.class)),
        // DTO로 변환시킨 후 리스트화
            Collectors.mapping(tuple -> new EmotionCountDTO(tuple.get(2, String.class), tuple.get(3, Long.class)),
                Collectors.toList())))
        // 순회한 후 객체 변환
        .entrySet().stream().map(e -> new UserEmotionCountDTO(e.getKey().getKey(), e.getKey().getValue(), e.getValue()))
        .collect(Collectors.toList());

    return new EmotionStatResponse(totalEmotionCounts, userEmotionCounts);
  }

}
