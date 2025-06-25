package com.diary.domain.diary.dto;

import java.util.List;

import com.diary.domain.member.dto.MemberResponse;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
//일기장의 멤버들 조회 응답저리
public class DiaryMemberResponse {
    private Long id;
    private String name;
    private List<MemberResponse> members;
}
