package com.diary.domain.tag.controller;


import com.diary.domain.tag.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tags")
public class TagViewController {
    private final TagService tagService;

    public TagViewController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String viewTags(Model model) {
        model.addAttribute("tags", tagService.getAllTags());
        return "tag_view";   // templates/tag_view.html 을 렌더링
    }
}
