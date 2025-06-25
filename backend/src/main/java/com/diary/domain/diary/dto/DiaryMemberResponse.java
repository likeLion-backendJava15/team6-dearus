package com.diary.domain.diary.dto;

import java.util.List;

import com.diary.domain.member.dto.MemberResponse;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder

public class DiaryDetailResponse {
    private Long id;
    private String name;
    private List<MemberResponse> members;
}
