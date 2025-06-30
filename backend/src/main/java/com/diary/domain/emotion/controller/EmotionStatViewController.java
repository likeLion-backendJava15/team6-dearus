package com.diary.domain.emotion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emotion")
public class EmotionStatViewController {

    // 감정 통계 페이지 렌더링
    @GetMapping("/stats")
    public String emotionStatsPage() {
        return "emotion_stats";  
    }
}