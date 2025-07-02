package com.diary.domain.comment.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CommentPageController {

    @GetMapping("/entry/{entryId}/comment-page")
    public String showCommentPage(@PathVariable Long entryId, Model model) {
        model.addAttribute("entryId", entryId); // 💡 Thymeleaf에서 사용될 entryId 전달
        return "comment_section"; 
    }
}