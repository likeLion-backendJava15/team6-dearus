package com.diary.domain.diary.dto;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class DiaryCreateRequest { //일기장 생성 요청 처리
    private String name;
}
