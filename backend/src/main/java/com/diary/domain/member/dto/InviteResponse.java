package com.diary.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class InviteResponse {
    private Long diaryId;
    private String diaryName;
    private String inviter;
}
