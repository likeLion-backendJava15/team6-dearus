package com.diary.domain.diary.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.service.DiaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // 다이어리 생성
    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(@RequestParam Long memberId,
                                                    @RequestBody DiaryCreateRequest requestDto) {
        DiaryResponse response = diaryService.createDiary(memberId, requestDto);

        // Location: /api/diary/{id}
        return ResponseEntity
            .created(URI.create("/api/diary/" + response.getId()))
            .body(response);
    }

    // 다이어리 목록 조회
    @GetMapping
    public ResponseEntity<List<DiaryResponse>> getDiaryList(@RequestParam Long memberId) {
        return ResponseEntity.ok(diaryService.getDiaryList(memberId));
    }

    // 다이어리 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> getDiary(@PathVariable Long id,
                                                  @RequestParam Long memberId) {
        return ResponseEntity.ok(diaryService.getDiary(id, memberId));
    }

    // 다이어리 수정
    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> updateDiary(@PathVariable Long id,
                                                     @RequestParam Long memberId,
                                                     @RequestBody DiaryUpdateRequest requestDto) {
        return ResponseEntity.ok(diaryService.updateDiary(id, memberId, requestDto));
    }

    // 다이어리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return ResponseEntity.noContent().build();
    }
}
