package com.diary.domain.emotion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.diary.domain.emotion.entity.EmotionStat;
import com.diary.domain.emotion.repository.EmotionStatRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmotionStatService {

    private EmotionStatRepository emotionStatRepository;

    public List<EmotionStat> findAll(){
        return emotionStatRepository.findAll();
    }
    
}
