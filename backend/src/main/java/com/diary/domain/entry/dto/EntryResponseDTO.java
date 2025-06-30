package com.diary.domain.entry.dto;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EntryResponseDTO { // 상세 조회
    private Long id;
    private Long diaryId;
    private Long authorId;
    private String authorNickname;
    private String title;
    private String content;
    private String emotion;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String emotionEmoji;
}

