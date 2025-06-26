package com.diary.domain.emotion.repository;

import java.time.LocalDateTime;

import com.diary.domain.emotion.dto.EmotionStatResponse;

public interface EmotionStatRepositoryCustom {
    EmotionStatResponse getEmotionStatResponse(Long diaryId, LocalDateTime start, LocalDateTime end);
}