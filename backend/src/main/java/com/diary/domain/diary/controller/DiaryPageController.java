package com.diary.domain.diary.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.diary.service.DiaryService;
import com.diary.domain.member.repository.DiaryMemberRepository;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.global.auth.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryPageController {

    private final DiaryService diaryService;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;

    @GetMapping
    public String showDiaryListPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // 현재 로그인한 사용자 ID 가져오기
        Long memberId = userDetails.getMember().getId();
        List<DiaryResponse> myDiaries = diaryService.getMyDiaries(memberId);
        // String userId = userDetails.getUsername();

        // // 사용자 조회
        // Member member = memberRepository.findByUserId(userId)
        //         .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // List<Diary> diaries = diaryMemberRepository.findAllByMember(member).stream()
        //         .map(DiaryMember::getDiary)
        //         .filter(diary -> !diary.getIsDeleted())
        //         .toList();

        // model.addAttribute("diaries", diaries);
        model.addAttribute("diaries", myDiaries);
        return "diary_list"; // templates/diary_list.html
    }

    @GetMapping("/form")
    public String showDiaryForm(
        @RequestParam(value = "editId", required = false) Long editId,
        Model model,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    DiaryCreateRequest requestDto = new DiaryCreateRequest();

    if (editId != null) {
        Diary diary = diaryRepository.findById(editId)
            .orElseThrow(() -> new IllegalArgumentException("해당 일기장을 찾을 수 없습니다."));

        // 작성자 본인인지 확인
        if (!diary.getOwner().getId().equals(userDetails.getId())) {
            try {
                throw new AccessDeniedException("수정 권한이 없습니다.");
            } catch (AccessDeniedException ex) {
            }
        }

        // DTO에 기존 내용 복사
        requestDto.setName(diary.getName());
        model.addAttribute("editMode", true);
        model.addAttribute("diaryId", diary.getId());
    } else {
        model.addAttribute("editMode", false);
    }

    model.addAttribute("diary", requestDto);
    return "diary_form";
}

    @PostMapping("/form")
public String createDiaryFromForm(@ModelAttribute DiaryCreateRequest request, Authentication auth) {
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Long memberId = userDetails.getMember().getId();

    diaryService.createDiary(memberId, request);

    return "redirect:/diary"; // 생성 후 목록 페이지로 이동
}


}
