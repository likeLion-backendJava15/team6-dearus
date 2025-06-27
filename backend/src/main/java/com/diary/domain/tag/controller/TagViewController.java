package com.diary.domain.tag.controller;

import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tags")
public class TagViewController {
    private final TagService tagService;

    public TagViewController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String viewTags(
            @RequestParam(required = false) Long editTagId,
            @RequestParam(required = false) Long selectedTagId,
            Model model
    ) {
        // 전체 태그 and 파라미터 태그 아이디를 모델에 올려줌
        List<TagResponse> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("editTagId", editTagId);
        model.addAttribute("selectedTagId", selectedTagId);

        // (선택된 태그의 일기 보기 로직이 필요하다면)
        if (selectedTagId != null) {
            // entries 가져오기
            List<EntryResponseDTO> entries = tagService.getEntriesByTag(selectedTagId);
            model.addAttribute("entries", entries);
            // Tag 이름 가져오기
            TagResponse selected = tags.stream()
                    .filter(t -> t.getId().equals(selectedTagId))
                    .findFirst().orElse(null);
            model.addAttribute("selectedTag", selected);
        }

        return "tag_view";
    }

    @PostMapping
    public String create(@RequestParam String name) {
        tagService.createTag(new TagRequest(name));
        return "redirect:/tags";
    }

    // ★ 수정: HTTP POST로 매핑 변경
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        tagService.updateTag(id, new TagRequest(name));
        return "redirect:/tags";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        tagService.deleteTag(id);
        return "redirect:/tags";
    }
}
