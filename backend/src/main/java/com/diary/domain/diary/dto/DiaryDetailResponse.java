package com.diary.domain.diary.dto;

import java.util.List;

import com.diary.domain.entry.dto.EntryResponseDTO;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
//일장장의 일기들 조회 응답 처리
public class DiaryDetailResponse {
    private Long id;
    private String name;
    private List<EntryResponseDTO> entries;
}

