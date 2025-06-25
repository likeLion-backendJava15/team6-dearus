package com.diary.domain.entry.dto;

import com.diary.domain.entry.enums.Emotion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryUpdateRequestDTO {
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 비워둘 수 없습니다.")
    private String content;

    @NotBlank(message = "감정을 선택해주세요.")
    private Emotion emotion;

    private String imageUrl; // 변경 가능
}
