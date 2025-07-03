package com.diary.domain.emotion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.AbstractMap;

import org.springframework.stereotype.Repository;
import com.querydsl.core.Tuple;

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
    QDiaryEntry diaryEntry = QDiaryEntry.diaryEntry;

    @Override
    public EmotionStatResponse getEmotionStatResponse(Long diaryId, Long userId, LocalDateTime start, LocalDateTime end) {
        List<EmotionCountDTO> totalEmotionCounts = getTotalEmotionStat(diaryId, start, end);
        List<UserEmotionCountDTO> userEmotionCounts = getUserEmotionStatList(diaryId, start, end);
        UserEmotionCountDTO userEmotionCount = getUserEmotionStat(diaryId, userId, start, end);
        return new EmotionStatResponse(totalEmotionCounts, userEmotionCounts, userEmotionCount);
    }

    @Override
    public List<EmotionCountDTO> getTotalEmotionStat(Long diaryId, LocalDateTime start, LocalDateTime end) {
        return queryFactory.select(diaryEntry.emotion.stringValue(), diaryEntry.count())
                .from(diaryEntry)
                .where(diaryEntry.diary.id.eq(diaryId).and(start != null ? diaryEntry.createdAt.goe(start) : null)
                        .and(end != null ? diaryEntry.createdAt.loe(end) : null))
                .groupBy(diaryEntry.emotion).fetch().stream()
                // DTO로 변환
                .map(tuple -> new EmotionCountDTO(tuple.get(0, String.class), tuple.get(1, Long.class)))
                .collect(Collectors.toList());
    }

    // 유저별 감정 통계
    @Override
    public List<UserEmotionCountDTO> getUserEmotionStatList(Long diaryId, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .select(diaryEntry.author.id, diaryEntry.author.nickname, diaryEntry.emotion.stringValue(),
                        diaryEntry.count())
                .from(diaryEntry).where(diaryEntry.diary.id.eq(diaryId)
                        .and(start != null ? diaryEntry.createdAt.goe(start) : null)
                        .and(end != null ? diaryEntry.createdAt.loe(end) : null))
                .groupBy(diaryEntry.author.id, diaryEntry.author.nickname, diaryEntry.emotion)
                .fetch().stream()
                // {(userid, nickname) : ["행복", 3]} 형태로 재배치
                .collect(Collectors.groupingBy(tuple -> new AbstractMap.SimpleEntry<>(tuple.get(0, Long.class),
                        tuple.get(1, String.class)),
                        // DTO로 변환시킨 후 리스트화
                        Collectors.mapping(
                                tuple -> new EmotionCountDTO(tuple.get(2, String.class), tuple.get(3, Long.class)),
                                Collectors.toList())))
                // 순회한 후 객체 변환
                .entrySet().stream()
                .map(e -> new UserEmotionCountDTO(e.getKey().getKey(), e.getKey().getValue(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public UserEmotionCountDTO getUserEmotionStat(Long diaryId, Long userId, LocalDateTime start, LocalDateTime end) {
        List<Tuple> results = queryFactory
                .select(diaryEntry.author.id, diaryEntry.author.nickname, diaryEntry.emotion.stringValue(),
                        diaryEntry.count())
                .from(diaryEntry)
                .where(diaryEntry.diary.id.eq(diaryId).and(diaryEntry.author.id.eq(userId))
                        .and(start != null ? diaryEntry.createdAt.goe(start) : null)
                        .and(end != null ? diaryEntry.createdAt.loe(end) : null))
                .groupBy(diaryEntry.emotion, diaryEntry.author.id, diaryEntry.author.nickname).fetch();

        if (results.isEmpty()) {
            return new UserEmotionCountDTO(userId, "", List.of());
        }
        String nickname = results.get(0).get(diaryEntry.author.nickname); // 첫 row에서 닉네임 추출
        List<EmotionCountDTO> emotionCounts = results.stream()
                .map(tuple -> new EmotionCountDTO(
                        tuple.get(diaryEntry.emotion.stringValue()),
                        tuple.get(diaryEntry.count())))
                .collect(Collectors.toList());

        return new UserEmotionCountDTO(userId, nickname, emotionCounts);

    }

}
