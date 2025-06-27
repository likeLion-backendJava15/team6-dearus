package com.diary.domain.emotion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.diary.domain.emotion.dto.EmotionCountDTO;
import com.diary.domain.emotion.dto.EmotionStatResponse;
import com.diary.domain.emotion.repository.EmotionStatRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmotionStatService {

    private final EmotionStatRepositoryCustom emotionStatRepository;

    public EmotionStatResponse getEmotionStatistics(Long diaryId, LocalDateTime start, LocalDateTime end){
        EmotionStatResponse response = emotionStatRepository.getEmotionStatResponse(diaryId, start, end);

        List<EmotionCountDTO> totalCounts = response.getTotalcounts();
        Long total = totalCounts.stream().mapToLong(EmotionCountDTO::getCount).sum();
        
        if(total > 0){
            for(EmotionCountDTO dto : totalCounts){
                double percent = (dto.getCount() * 100.0) / total;
                dto.setPercent(Math.round(percent * 10.0) / 10.0);
            }
        }
        
        return response;

    }
    
    
    
}
