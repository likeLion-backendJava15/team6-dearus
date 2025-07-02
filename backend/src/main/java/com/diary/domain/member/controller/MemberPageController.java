package com.diary.domain.member.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.diary.domain.member.dto.MemberResponse;
import com.diary.domain.member.service.MemberInviteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberPageController {

    private final MemberInviteService memberService;

    @GetMapping("/diary/{diaryId}/members")
    public String getManageMembersPage(@PathVariable Long diaryId, Model model) {
        List<MemberResponse> members = memberService.getAcceptedMembers(diaryId);
        model.addAttribute("members", members);
        model.addAttribute("diaryId", diaryId);
        return "manage_members";
    }

}
