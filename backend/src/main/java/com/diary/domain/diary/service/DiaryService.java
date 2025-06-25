package com.diary.domain.diary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryMemberResponse;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.domain.member.service.MemberService;
import com.diary.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  // 생성자 의존성 주입 애너테이션
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final DiaryMemberRepository diaryMemberRepository;

    // 일기장 생성 구현
    public DiaryResponse createDiary(DiaryCreateRequest dto) {
        //(멤버 유저 엔티티 가정)
        DiaryMember currentMember = memberService.getCurrentUser(); // 현재 로그인 유저

        Diary diary = Diary.builder()
                .name(dto.getName())
                .build();
        Diary saved = diaryRepository.save(diary);
        
        //(현재 유저를 Owner로 멤버 추가 가정)
        memberService.addMemberToDiary(currentMember, saved, Role.OWNER);
        
        return toResponseDTO(saved);
    }

    // 유저 기준 일기장 목록 조회 구현
    public List<DiaryResponse> getMyDiaries() {
        // 현재 유저 객체 생성
        DiaryMember currentMember = memberService.getCurrentUser();

        // 해당 유저가 속한 일기장 목록 위임
        List<Diary> diaries = memberService.findDiariesByUser(currentMember);

        return diaries.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // 일기장 상세 조회 구현 (일기장 - 일기 목록)
    public DiaryMemberResponse getDiaryDetail(Long id) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new CustomException("일기장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return DiaryMemberResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .members(List.of()) // 나중에 멤버 연관관계 추가
                .build();
    }

    // 일기장 수정 구현
    public void updateDiary(Long id, DiaryUpdateRequest dto) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Diary not found"));
        diary.setName(dto.getName());
        diaryRepository.save(diary);
    }

    // 일기장 삭제 구현
    public void deleteDiary(Long id, Long memberId) {
        // 명시적으로 멤버 먼저 삭제 (보류 : 멤버 구현 필요)
        diaryMemberRepository.deleteById(memberId);
        
        // 일기장 삭제 → entries는 cascade로 자동 삭제됨
        diaryRepository.deleteById(id);
    }

    private DiaryResponse toResponseDTO(Diary diary) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .name(diary.getName())
                .build();
    }
}
