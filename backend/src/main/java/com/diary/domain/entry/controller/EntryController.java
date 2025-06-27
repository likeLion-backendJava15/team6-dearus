package com.diary.domain.entry.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diary.domain.entry.dto.EntryCreateRequestDTO;
import com.diary.domain.entry.dto.EntryListResponseDTO;
import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.dto.EntryUpdateRequestDTO;
import com.diary.domain.entry.service.EntryService;
import com.diary.global.auth.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EntryController {
    
    private final EntryService entryService;

    // 일기 작성 - 로그인한 사용자 기반
    @PostMapping("/entry")
    public ResponseEntity<Map<String, Object>> createEntry(@RequestBody EntryCreateRequestDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long authorId = userDetails.getId();  // 로그인된 사용자 ID
        Long entryId = entryService.createEntry(requestDTO, authorId);  // 서비스 호출

        // 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("id", entryId);
        response.put("message", "작성완료");

        return  ResponseEntity.ok(response);
    }

    // 일기 목록 조회
    @GetMapping("/diary/{diaryId}/entries")
    public ResponseEntity<List<EntryListResponseDTO>> getEntriesByDiary (@PathVariable Long diaryId) {
        List<EntryListResponseDTO> entries = entryService.getAllEntriesByDiaryId(diaryId);
        return ResponseEntity.ok(entries);
    }

    // 일기 상세 조회
    @GetMapping("/entry/{entryId}")
    public ResponseEntity<EntryResponseDTO> getEntry (@PathVariable Long entryId) {
        EntryResponseDTO dto = entryService.getEntryDetail(entryId);
        return ResponseEntity.ok(dto);
    }
    
    // 일기 수정
    @PutMapping("/entry/{entryId}")
    public ResponseEntity<Map<String, Object>> updateEntry (@PathVariable Long entryId, @RequestBody EntryUpdateRequestDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getId();
        entryService.updateEntry(entryId, requestDTO, memberId);

        return ResponseEntity.ok(Map.of("message", "수정 완료"));
    }    

    @DeleteMapping("/entry/{entryId}")
    public ResponseEntity<Map<String, Object>> deleteEntry (@PathVariable Long entryId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getId();
        entryService.deleteEntry(entryId, memberId);

        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }
}
