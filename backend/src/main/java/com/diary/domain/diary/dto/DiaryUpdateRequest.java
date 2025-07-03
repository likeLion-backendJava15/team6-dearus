package com.diary.domain.diary.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//일기장명 수정 요청 처리
public class DiaryUpdateRequest {
    private String name;
}
