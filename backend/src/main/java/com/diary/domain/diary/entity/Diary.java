package com.diary.domain.diary.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.member.entity.DiaryMember;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diary")
@Builder
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long ownerId;  // Member 엔티티 대신 FK ID만 저장
    
    @Column(nullable = false)
    private Boolean isDeleted = false; // Soft-Delete

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryEntry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "diary", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryMember> members = new ArrayList<>();
    
}
