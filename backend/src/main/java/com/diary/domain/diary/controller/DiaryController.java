package com.diary.domain.diary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.service.DiaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor  // 생성자 의존성 주입 애너테이션
public class DiaryController {

    private final DiaryService diaryService;

    //일기장 생성 동작
    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(@RequestBody DiaryCreateRequest dto) {
        return ResponseEntity.ok(diaryService.createDiary(dto));
    }

    //일기장 목록 조회 동작
    @GetMapping
    public ResponseEntity<List<DiaryResponse>> getDiaryList() {
        return ResponseEntity.ok(diaryService.getDiaryList());
    }

    //일기장 단일 조회 동작
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
    }

    //일기장 수정 동작
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateDiary(@PathVariable Long id,
                                                           @RequestBody DiaryUpdateRequest dto) {
        diaryService.updateDiary(id, dto);
        return ResponseEntity.ok(Map.of("message : ", "수정 완료"));
    }

    //일기장 삭제 동작
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDiary(@PathVariable Long id, Long memberId) {
        diaryService.deleteDiary(id, memberId);
        return ResponseEntity.ok(Map.of("message : ", "삭제 완료"));
    }
}
