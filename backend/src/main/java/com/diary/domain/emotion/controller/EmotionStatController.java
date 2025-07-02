package com.diary.domain.emotion.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.diary.global.auth.CustomUserDetails;

import com.diary.domain.emotion.dto.EmotionStatResponse;
import com.diary.domain.emotion.service.EmotionStatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion")
public class EmotionStatController {

    private final EmotionStatService emotionStatService;

    @GetMapping("/{diaryId}/stat")
    public ResponseEntity<EmotionStatResponse> getUserEmotionStats(
            @PathVariable Long diaryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Long userId = userDetails.getId();
        EmotionStatResponse response = emotionStatService.getEmotionStatistics(diaryId, userId, start, end);
        return ResponseEntity.ok(response);
    }
}
