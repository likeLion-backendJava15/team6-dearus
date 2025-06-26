package com.diary.domain.tag.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 일기에 연결할 태그들의 ID 목록 담는 DTO
public class EntryTagsRequest {
    @NotEmpty(message = "태그 목록은 비어있을 수 없습니다")
    private List<Long> tagIds;
}