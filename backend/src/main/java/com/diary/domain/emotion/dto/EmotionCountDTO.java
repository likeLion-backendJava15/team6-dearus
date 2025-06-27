package com.diary.domain.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmotionCountDTO {
    private String emotion;
    private Long count;
    private Double percent = 0.0;

    public EmotionCountDTO(String emotion, Long count) {
        this.emotion = emotion;
        this.count = count;
        this.percent = 0.0;
    }
}