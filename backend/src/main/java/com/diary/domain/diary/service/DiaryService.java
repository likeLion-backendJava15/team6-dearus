package com.diary.domain.diary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryDetailResponse;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  // 생성자 의존성 주입 애너테이션
public class DiaryService {

    private final DiaryRepository diaryRepository;

    // 일기장 생성 구현
    public DiaryResponse createDiary(DiaryCreateRequest dto) {
        Diary diary = Diary.builder()
                .name(dto.getName())
                .build();
        Diary saved = diaryRepository.save(diary);
        return toResponseDTO(saved);
    }

    // 유저 기준 일기장 목록 조회 구현
    public List<DiaryResponse> getMyDiaries() {
        List<Diary> diaries = diaryRepository.findAll(); // 나중에 유저 기준으로 필터링 필요 (보류)
        return diaries.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // 일기장 상세 조회 구현 (일기장 - 일기 목록)
    public DiaryDetailResponse getDiaryDetail(Long id) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Diary not found"));
        return DiaryDetailResponse.builder()
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
    public void deleteDiary(Long id) {
        // 명시적으로 멤버 먼저 삭제 (보류 : 멤버 구현 필요)
        
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
