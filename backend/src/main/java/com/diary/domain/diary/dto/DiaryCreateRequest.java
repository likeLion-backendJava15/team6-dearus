package com.diary.domain.diary.dto;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
//일기장 생성 요청 처리
public class DiaryCreateRequest {
    private String name;
}
