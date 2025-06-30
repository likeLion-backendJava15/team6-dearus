package com.diary.domain.emotion.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.diary.domain.emotion.dto.EmotionCountDTO;
import com.diary.domain.emotion.dto.EmotionStatResponse;
import com.diary.domain.emotion.dto.UserEmotionCountDTO;

public interface EmotionStatRepositoryCustom {
    EmotionStatResponse getEmotionStatResponse(Long diaryId, Long userId, LocalDateTime start, LocalDateTime end);

    List<EmotionCountDTO> getTotalEmotionStat(Long diaryId, LocalDateTime start, LocalDateTime end);

    List<UserEmotionCountDTO> getUserEmotionStatList(Long diaryId, LocalDateTime start, LocalDateTime end);

    UserEmotionCountDTO getUserEmotionStat(Long diaryId, Long userId, LocalDateTime start, LocalDateTime end);
}