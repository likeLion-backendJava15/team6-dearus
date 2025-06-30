package com.diary.domain.entry.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.entry.dto.EntryListResponseDTO;
import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.service.EntryService;

@Controller
@RequiredArgsConstructor
public class EntryPageController {

    private final EntryService entryService;
    private final DiaryRepository diaryRepository;

    @GetMapping("/entry/list")
    public String showEntryList(@RequestParam Long diaryId, Model model) {
        // 일기장 정보 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기장을 찾을 수 없습니다."));

        // 일기 목록 조회
        List<EntryListResponseDTO> entryList = entryService.getAllEntriesByDiaryId(diaryId);

        // 모델에 담기
        model.addAttribute("diaryId", diaryId);
        model.addAttribute("diaryName", diary.getName());
        model.addAttribute("entryList", entryList);

        return "entry_list";  // templates/entry_list.html
    }

    @GetMapping("/entry/form")
    public String showEntryForm(@RequestParam(required = false) Long diaryId, Model model) {
        model.addAttribute("diaryId", diaryId); // 전달받은 일기장 ID 전달
        return "entry_form"; // templates/entry_form.html
    }

    @GetMapping("/entry/detail/{entryId}")
    public String showEntryDetail(@PathVariable Long entryId, Model model) {
        EntryResponseDTO dto = entryService.getEntryDetail(entryId);
        model.addAttribute("entry", dto);
        return "entry_detail";
    }

}
