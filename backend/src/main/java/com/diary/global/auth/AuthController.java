package com.diary.global.auth;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final AuthService authService;
    private final DiaryRepository diaryRepository;
    public AuthController(AuthService authService, DiaryRepository diaryRepository) {
        this.authService = authService;
        this.diaryRepository = diaryRepository;
    }
    // 로그인 상태라면 diary_list로, 아니면 환영 페이지
    @GetMapping("/")
    public String rootRedirect(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            List<Diary> diaries = diaryRepository.findAllByOwnerIdAndIsDeletedFalse(user.getId());

            return "redirect:/diary";
        }
        // 비로그인 사용자 혹은 다이어리가 하나도 없으면 welcome 뷰
        return "welcome";
    }

    // 로그인 페이지 (기존)
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 1) 회원가입 폼 표시
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    // 2) 회원가입 처리
    @PostMapping("/signup")
    public String signupSubmit(
            @Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
            BindingResult bindingResult
    ) {
        System.out.println("signupRequest: " + signupRequest);
        if (bindingResult.hasErrors()) {
            // 검증 실패하면 다시 폼으로
            return "signup";
        }

        try {
            authService.register(signupRequest);
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("userId", "error.signupRequest", e.getMessage());
            return "signup";
        }

        // 가입 성공 시 로그인 페이지로 리다이렉트
        return "redirect:/login?registered";
    }
}
