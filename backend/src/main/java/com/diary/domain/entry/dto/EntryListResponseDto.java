package com.diary.domain.entry.dto;

import java.time.LocalDateTime;

import com.diary.domain.entry.enums.Emotion;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EntryListResponseDTO { // 목록 조회
    private Long id;
    private String title;
    private Emotion emotion;
    private String imageUrl;
    private String authorNickname;
    private LocalDateTime createdAt;
}
