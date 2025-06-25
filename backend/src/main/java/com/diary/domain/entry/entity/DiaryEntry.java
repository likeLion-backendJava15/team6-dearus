package com.diary.domain.entry.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.entry.enums.Emotion;
import com.diary.domain.member.entity.Member;

@Entity
@Table(name = "Diary_Entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Diary:Entry(1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    // User:Entry(1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    // 감정 ENUM
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    // 제목
    @Column(length = 100)
    private String title;

    // 본문
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    // 대표 이미지 URL
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 태그
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
  

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content, String imageUrl, Emotion emotion) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.emotion = emotion;
    }
}
