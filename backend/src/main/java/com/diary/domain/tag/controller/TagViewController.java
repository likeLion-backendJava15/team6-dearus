package com.diary.domain.tag.controller;

import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.service.TagService;
import com.diary.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagViewController {

    private final TagService tagService;
    private final MemberRepository memberRepository;

    @GetMapping
    public String viewTags(
            @RequestParam(required = false) Long editTagId,
            @RequestParam(required = false) Long selectedTagId,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = getMember(userDetails);

        List<TagResponse> tags = tagService.getAllTags(member);
        model.addAttribute("tags", tags);
        model.addAttribute("editTagId", editTagId);
        model.addAttribute("selectedTagId", selectedTagId);

        if (selectedTagId != null) {
            List<EntryResponseDTO> entries = tagService.getEntriesByTag(selectedTagId, member);
            model.addAttribute("entries", entries);
            TagResponse selected = tags.stream()
                    .filter(t -> t.getId().equals(selectedTagId))
                    .findFirst().orElse(null);
            model.addAttribute("selectedTag", selected);
        }

        return "tag_view";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = getMember(userDetails);
        tagService.createTag(new TagRequest(name), member);
        return "redirect:/tags";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = getMember(userDetails);
        tagService.updateTag(id, new TagRequest(name), member);
        return "redirect:/tags";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = getMember(userDetails);
        tagService.deleteTag(id, member);
        return "redirect:/tags";
    }

    private Member getMember(CustomUserDetails userDetails) {
        return memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 정보 없음"));
    }
}
