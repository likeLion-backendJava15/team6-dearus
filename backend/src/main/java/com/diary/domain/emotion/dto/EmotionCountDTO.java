package com.diary.domain.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmotionCountDTO {
    private String emotion;
    private Long count;
}
