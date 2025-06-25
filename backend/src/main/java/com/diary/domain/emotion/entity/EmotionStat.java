package com.diary.domain.emotion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diary_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime creatAt;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "diary_id", nullable = false)
    private Long diaryId;

}
