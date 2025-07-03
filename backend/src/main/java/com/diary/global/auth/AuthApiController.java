package com.diary.global.auth;

import lombok.RequiredArgsConstructor;

import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUserId(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "userId", user.getUsername(),   // ✅ DB의 userId
            "nickname", user.getNickname(), // ✅ 닉네임
            "email", user.getEmail()       // ✅ 이메일
        ));
    }
}