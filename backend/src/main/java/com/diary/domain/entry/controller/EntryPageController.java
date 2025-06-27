package com.diary.domain.entry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.service.EntryService;

@Controller
@RequiredArgsConstructor
public class EntryPageController {

    private final EntryService entryService;

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
