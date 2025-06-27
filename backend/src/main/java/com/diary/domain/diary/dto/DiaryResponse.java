package com.diary.domain.diary.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//일기장 생성, 기본조회 응답 처리
public class DiaryResponse {
    private Long id;
    private String name;
}
