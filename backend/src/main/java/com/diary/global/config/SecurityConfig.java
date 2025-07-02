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
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getId();

        // 사용자의 다이어리 리스트 조회
        List<Diary> diaries = diaryRepository.findAllByOwnerIdAndIsDeletedFalse(userId);

        // 리다이렉트할 URI 결정
        String target = "/diary";
        // if (diaries.isEmpty()) {
        //     // 다이어리가 하나도 없으면, 다이어리 생성 전용 뷰로
        //     // (원하시면 /welcome 으로 보낼 수도 있고, 바로 /diary 로 보내서 빈 목록 보여줘도 됩니다)
        //     target = "/diary";
        // } else {
        //     // 이미 다이어리가 있으면, 첫 번째 일기장 클릭 시 진입하는 엔트리 리스트 뷰로
        //     // 우리가 새로 만든 뷰 전용 엔드포인트
        //     target = "/diary/" + diaries.get(0).getId() + "/entries";
        // }

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (isAjax) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("redirectUri", target)
            );
        } else {
            response.sendRedirect(target);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
