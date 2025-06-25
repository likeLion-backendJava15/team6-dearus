package com.diary.domain.tag.controller;

import com.diary.domain.tag.dto.EntryTagsRequest;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/tag")
    public ResponseEntity<TagResponse> createTag(
            @Valid @RequestBody TagRequest request
    ) {
        // 태그 생성 API
        TagResponse created = tagService.createTag(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)     // 201 Created
                .body(created);                 // { "id":…, "name":… }
    }

    @GetMapping("/tag")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        // 모든 태그 조회 API
        List<TagResponse> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    /** [3] 일기-태그 연결 **/
    @PostMapping("/entry/{entryId}/tags")
    public ResponseEntity<Map<String, String>> linkTagsToEntry(
            @PathVariable Integer entryId,
            @Valid @RequestBody EntryTagsRequest request
    ) {
        // 일기에 태그 연결 API
        tagService.linkTagsToEntry(entryId, request);
        return ResponseEntity
                .ok(Map.of("message", "연결 완료"));
    }

    /** [4] 일기-태그 제거 **/
    @DeleteMapping("/entry/{entryId}/tags/{tagId}")
    public ResponseEntity<Map<String, String>> removeTagFromEntry(
            @PathVariable Integer entryId,
            @PathVariable Integer tagId
    ) {
        // 일기에서 태그 제거 API
        tagService.removeTagFromEntry(entryId, tagId);
        return ResponseEntity
                .ok(Map.of("message", "삭제 완료"));
    }
}
