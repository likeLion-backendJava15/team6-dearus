package com.diary.domain.diary.dto;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class DiaryCreateRequest {
    private String name;
}
