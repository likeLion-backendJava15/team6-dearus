package com.diary;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.DiaryMemberId;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;

@SpringBootApplication
public class DearUsApplication {

    @Bean
    @Order(1)
    public CommandLineRunner initTestUser(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (memberRepository.findByUserId("testuser").isEmpty()) {
                Member member = Member.builder()
                        .userId("testuser")
                        .nickname("테스트유저")
                        .password(passwordEncoder.encode("testpass"))
                        .email("test1@example.com")
                        .build();
                memberRepository.save(member);
            }

            if (memberRepository.findByUserId("user3").isEmpty()) {
                Member member = Member.builder()
                        .userId("user3")
                        .nickname("테스트유저2")
                        .password(passwordEncoder.encode("test"))
                        .email("test2@example.com")
                        .build();
                memberRepository.save(member);
            }
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner initTestDiary(
            DiaryRepository diaryRepository,
            MemberRepository memberRepository,
            DiaryMemberRepository diaryMemberRepository) {

        return args -> {
        Member owner = memberRepository.findByUserId("testuser")
            .orElseThrow(() -> new RuntimeException("testuser 없음"));

        if (diaryRepository.findAll().isEmpty()) {
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
