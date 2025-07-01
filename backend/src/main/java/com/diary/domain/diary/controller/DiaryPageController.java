package com.diary.domain.diary.controller;

import com.diary.domain.diary.service.DiaryService;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DiaryPageController {

    private final DiaryService diaryService;
    private final MemberRepository memberRepository;

    @GetMapping("/diary")
    public String showDiaryListPage(Model model) {
        // 로그인한 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername(); // userId가 저장됨

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        model.addAttribute("diaries", diaryService.findAllByMember(member.getId()));
        return "diary/list"; // templates/diary/list.html
    }

}
