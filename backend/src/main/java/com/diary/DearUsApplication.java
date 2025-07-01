package com.diary;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;

@SpringBootApplication
public class DearUsApplication {

    @Bean
    public CommandLineRunner initTestUser(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (memberRepository.findByUserId("testuser").isEmpty()) {
                Member member = new Member();
                member.setUserId("testuser");
                member.setNickname("테스트유저");
                member.setPassword(passwordEncoder.encode("testpass"));
                member.setEmail("123@naver.com");
                memberRepository.save(member);
            }
        };
    }

    @Bean
    public CommandLineRunner initTestDiary(DiaryRepository diaryRepository, MemberRepository memberRepository) {
        return args -> {
            if (diaryRepository.findAll().isEmpty()) {
                Member owner = memberRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("사용자 없음"));
                Diary diary = new Diary();
                diary.setName("테스트일기장");
                diary.setOwner(owner);
                diaryRepository.save(diary);
                System.out.println("✅ 테스트용 일기장 생성 완료!");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(DearUsApplication.class, args);
    }

}
