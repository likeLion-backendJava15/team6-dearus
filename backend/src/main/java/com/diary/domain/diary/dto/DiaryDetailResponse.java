package com.diary.domain.diary.dto;

import java.util.List;

import com.diary.domain.entry.dto.EntryResponse;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder

public class DiaryDetailResponse {
    private Long id;
    private String name;
    private List<EntryResponse> entries;
}

