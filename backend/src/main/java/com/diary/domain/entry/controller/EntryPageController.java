package com.diary.domain.entry.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.entry.dto.EntryListResponseDTO;
import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.enums.Emotion;
import com.diary.domain.entry.service.EntryService;
import com.diary.global.auth.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequiredArgsConstructor
public class EntryPageController {

    private final EntryService entryService;
    private final DiaryRepository diaryRepository;

    @GetMapping("/entry/list")
    public String showEntryList(@RequestParam Long diaryId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 일기장 정보 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기장을 찾을 수 없습니다."));

        Long userId = userDetails.getId();

        // 일기 목록 조회
        List<EntryListResponseDTO> entryList = entryService.getAllEntriesByDiaryId(diaryId, userId);

        // 모델에 담기
        model.addAttribute("loginMemberId", userDetails.getId());
        model.addAttribute("diaryId", diaryId);
        model.addAttribute("diaryName", diary.getName());
        model.addAttribute("entryList", entryList);

        return "entry_list";  // templates/entry_list.html
    }

    // 일기 생성
    @GetMapping("/entry/form")
    public String showEntryForm(@RequestParam(required = false) Long diaryId, Model model) {
        model.addAttribute("diaryId", diaryId); // 전달받은 일기장 ID 전달
        model.addAttribute("emotions", Emotion.values());

        return "entry_form"; // templates/entry_form.html
    }

    // 일기 상세 보기
    @GetMapping("/entry/detail/{entryId}")
    public String showEntryDetail(@PathVariable Long entryId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        
        EntryResponseDTO dto = entryService.getEntryDetail(entryId, userId);
        model.addAttribute("entry", dto);
        model.addAttribute("loginMemberId", userDetails.getId());
        return "entry_detail";
    }

    // 일기 수정
    @GetMapping("/entry/edit/{entryId}")
    public String showEditForm(@PathVariable Long entryId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        
        EntryResponseDTO entry = entryService.getEntryDetail(entryId, userId);
        ObjectMapper objectMapper = new ObjectMapper();
        String tagsJson = "[]";
        try {
            tagsJson = objectMapper.writeValueAsString(entry.getTags());
        } catch (JsonProcessingException e) {
            // 필요 시 로그 남기기
            e.printStackTrace();
        }

        model.addAttribute("entry", entry);                  // 기존 entry 데이터 전달
        model.addAttribute("diaryId", entry.getDiaryId());   // 일기장 ID도 함께 전달
        model.addAttribute("emotions", Emotion.values());
        model.addAttribute("editMode", true);                  // 수정 모드 플래그
        model.addAttribute("entryTagsJson", tagsJson);
        return "entry_form";  // 기존 entry_form.html 그대로 사용
    }

    // 일기 삭제
    @PostMapping("/entry/delete/{entryId}")
    public String deleteEntry(@PathVariable Long entryId,
            @RequestParam Long diaryId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        entryService.deleteEntry(entryId, userDetails.getId());
        return "redirect:/entry/list?diaryId=" + diaryId;
    }

}
