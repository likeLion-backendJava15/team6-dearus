package com.diary.domain.member.entity;

import java.time.LocalDateTime;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diary_member")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DiaryMember {
    @EmbeddedId
    private DiaryMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diaryId")
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Role role = Role.GUEST;

    @Column(name = "invited_at")
    @Builder.Default
    private LocalDateTime invitedAt = LocalDateTime.now();

    @Column(name = "accepted")
    @Builder.Default
    private boolean accepted = false;

    public enum Role {
        OWNER, GUEST
    }
}
