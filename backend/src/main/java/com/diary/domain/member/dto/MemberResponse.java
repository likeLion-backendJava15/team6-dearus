package com.diary.domain.member.dto;

import com.diary.domain.member.entity.DiaryMember.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String nickname;
    private Role role;  // OWNER, GUEST 등
}
