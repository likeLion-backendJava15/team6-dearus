package com.diary.domain.emotion.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diary.domain.emotion.dto.EmotionStatResponse;
import com.diary.domain.emotion.service.EmotionStatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion")
public class EmotionStatController {

    private final EmotionStatService emotionStatService;

    @GetMapping("/{diaryId}/user/{userId}/stat")
    public ResponseEntity<EmotionStatResponse> getUserEmotionStats(
            @PathVariable Long diaryId,
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        EmotionStatResponse response = emotionStatService.getEmotionStatistics(diaryId, userId, start, end);
        return ResponseEntity.ok(response);
    }
}
