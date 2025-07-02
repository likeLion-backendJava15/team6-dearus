package com.diary.domain.member.controller;

import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 루트 요청 시 로그인 페이지로 리다이렉트
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }

    // 로그인
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    // 회원가입 폼
    @GetMapping("/register")
    public String registerForm() {
        return "signup"; // templates/signup.html
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@RequestParam String userId,
                                @RequestParam String nickname,
                                @RequestParam String password,
                                @RequestParam String email,
                                Model model) {

        // 중복 아이디 체크
        if (memberRepository.findByUserId(userId).isPresent()) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "register";
        }

        // 사용자 저장
        Member member = new Member();
        member.setUserId(userId);
        member.setNickname(nickname);
        member.setPassword(passwordEncoder.encode(password));
        member.setEmail(email);

        memberRepository.save(member);

        // 회원가입 성공 시 로그인 페이지로 리다이렉트
        return "redirect:/login";
    }

}
