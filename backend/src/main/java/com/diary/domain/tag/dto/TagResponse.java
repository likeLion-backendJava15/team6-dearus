package com.diary.domain.tag.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 클라이언트에게 반환할 태그 정도 담는 DTO
// 태그 ID , 이름 전달
@Getter
@RequiredArgsConstructor
public class TagResponse {
    private final Integer id;
    private final String name;
}
