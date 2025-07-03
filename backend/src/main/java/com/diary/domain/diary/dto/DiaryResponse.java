package com.diary.domain.diary.dto;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.member.entity.DiaryMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryResponse {

    private Long id;
    private String name;
    private Long ownerId;
    private String role;
    private boolean isOwner;

    public static DiaryResponse from(Diary diary, DiaryMember diaryMember) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .ownerId(diary.getOwner().getId())
                .role(diaryMember.getRole().name())
                .isOwner(diaryMember.getRole() == DiaryMember.Role.OWNER)
                .build();
    }
}
