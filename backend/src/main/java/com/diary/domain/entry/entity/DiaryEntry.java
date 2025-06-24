package com.diary.domain.entry.entity;

import com.diary.domain.tag.entity.Tag;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Diary_Entry")
public class DiaryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 기존 필드들…

    // ↙ 이 부분을 추가
    @ManyToMany
    @JoinTable(
            name = "Entry_Tag",
            joinColumns = @JoinColumn(name = "entry_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // getters & setters에 tags 추가
    public Set<Tag> getTags() {
        return tags;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
