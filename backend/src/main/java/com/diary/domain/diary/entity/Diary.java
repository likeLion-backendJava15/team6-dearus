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
@Builder
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryEntry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "diary", fetch = FetchType.LAZY)
    private List<DiaryMember> members = new ArrayList<>();
    
}
