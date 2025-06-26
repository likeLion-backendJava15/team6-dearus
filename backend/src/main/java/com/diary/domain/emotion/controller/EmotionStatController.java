package com.diary.domain.emotion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diary.domain.emotion.entity.EmotionStat;
import com.diary.domain.emotion.service.EmotionStatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion")
public class EmotionStatController {

    private final EmotionStatService emotionStatService;

    @GetMapping("/test")
    public List<EmotionStat> getAllStats() {
        return emotionStatService.findAll();
    }
}
