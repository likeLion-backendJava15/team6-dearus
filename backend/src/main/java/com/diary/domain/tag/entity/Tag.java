package com.diary.domain.tag.entity;

import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name; // 중복 불가 태그 이름

    @ManyToMany(mappedBy = "tags", fetch=FetchType.LAZY) //일기와 태그는 다대다 관계이며, DiaryEntry 클래스의 tags 필드에 의해 매핑됨
    private Set<DiaryEntry> entries = new HashSet<>(); // 태그 연결된 일기 목록들

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 이 태그를 만든 사람

    public Tag(String name,Member member) {
        this.name = name;
        this.member = member;
    }
}
