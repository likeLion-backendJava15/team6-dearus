package com.diary.domain.emotion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.diary.domain.diary.service.DiaryService;

import com.diary.global.auth.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emotion")
public class EmotionStatViewController {

    private final DiaryService diaryService;

    @GetMapping("/stats")
    public String emotionStatsPage(@RequestParam Long diaryId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        diaryService.getDiary(diaryId, userId);

        model.addAttribute("diaryId", diaryId);
        return "emotion_stats";
    }
}