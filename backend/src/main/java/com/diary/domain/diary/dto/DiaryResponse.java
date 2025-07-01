package com.diary.domain.diary.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryResponse {
    private Long id;
    private String name;
    private Long ownerId;
}
