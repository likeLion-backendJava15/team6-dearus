package com.diary.domain.tag.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagResponse {
    private final Integer id;
    private final String name;
}
