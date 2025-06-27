package com.diary.domain.emotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diary.domain.emotion.entity.EmotionStat;

public interface EmotionStatRepository extends JpaRepository<EmotionStat, Long> {
    
}