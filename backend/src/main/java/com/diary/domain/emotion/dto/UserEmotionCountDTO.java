package com.diary.domain.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class UserEmotionCountDTO {
    private Long userId;
    private String nickname;
    private List<EmotionCountDTO> emotionCounts;
}
