package com.diary.domain.entry.dto;

import com.diary.domain.entry.enums.Emotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryCreateRequestDTO {

    @NotNull(message = "일기장 ID는 필수입니다.")
    private Long diaryId;

    @NotBlank(message = "제목을 비워둘 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 비워둘 수 없습니다.")
    private String content;
    
    @NotBlank(message = "감정을 선택해주세요.")
    private Emotion emotion;

    private String imageUrl;
}
