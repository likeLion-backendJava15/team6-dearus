package com.diary.domain.emotion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.diary.domain.diary.service.DiaryService;
import com.diary.domain.member.service.MemberInviteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emotion")
public class EmotionStatViewController {

    private final DiaryService diaryService;
    private final MemberInviteService memberInviteService;

    @GetMapping("/stats")
    public String emotionStatsPage(@RequestParam Long diaryId, Model model) {
        Long userId = memberInviteService.getCurrentUserId();
        diaryService.getDiary(diaryId, userId);

        model.addAttribute("diaryId", diaryId);
        model.addAttribute("userId", userId);
        return "emotion_stats";
    }
}