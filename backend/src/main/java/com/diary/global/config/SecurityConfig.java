package com.diary.global.config;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.global.auth.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DiaryRepository diaryRepository;
    private final ObjectMapper objectMapper;  // Jackson

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    private void loginSuccessHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth
    ) throws IOException {
        // 1) 유저 정보
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getId();

        // 2) 첫 번째 일기장 ID 조회
        List<Diary> diaries = diaryRepository.findAllByOwnerIdAndIsDeletedFalse(userId);
        String target = diaries.isEmpty()
                ? "/entry/list"              // 비어 있어도 entry_list 뷰 호출
                : "/entry/list?diaryId=" + diaries.get(0).getId();

        // 3) AJAX 여부 검사
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (isAjax) {
            // JSON으로 내려줌
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("redirectUri", target)
            );
        } else {
            // 비-AJAX 이면 브라우저가 302를 따라가게
            response.sendRedirect(target);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
