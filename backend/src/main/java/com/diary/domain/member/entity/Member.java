package com.diary.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`user`") // ⚠️ 백틱으로 감싸야 예약어 충돌 방지됨
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;
}
