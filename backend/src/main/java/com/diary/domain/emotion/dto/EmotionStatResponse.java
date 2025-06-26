package com.diary.domain.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class EmotionStatResponse {
    private List<EmotionCountDTO> totalcounts; // 전체 일기 통계
    private List<UserEmotionCountDTO> usercounts; // 유저별 감정 통계
}
