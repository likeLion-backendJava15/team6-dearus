package com.diary.domain.tag.controller;

import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.domain.tag.dto.EntryTagsRequest;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.service.TagService;
import com.diary.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final MemberRepository memberRepository;

    // [1] 태그 생성
    @PostMapping("/tag")
    public ResponseEntity<TagResponse> createTag(
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = getMember(userDetails);
        TagResponse created = tagService.createTag(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [2] 태그 전체 조회
    @GetMapping("/tag")
    public ResponseEntity<List<TagResponse>> getAllTags(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = getMember(userDetails);
        List<TagResponse> tags = tagService.getAllTags(member);
        return ResponseEntity.ok(tags);
    }

    // [3] 태그 수정
    @PutMapping("/tag/{tagId}")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = getMember(userDetails);
        TagResponse updated = tagService.updateTag(tagId, request, member);
        return ResponseEntity.ok(updated);
    }

    // [4] 태그 삭제
    @DeleteMapping("/tag/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable Long tagId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = getMember(userDetails);
        tagService.deleteTag(tagId, member);
        return ResponseEntity.noContent().build();
    }

    // [5] 태그별 일기 조회
    @GetMapping("/tag/{tagId}/entries")
    public ResponseEntity<List<EntryResponseDTO>> getEntriesByTag(
            @PathVariable Long tagId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = getMember(userDetails);
        List<EntryResponseDTO> entries = tagService.getEntriesByTag(tagId, member);
        return ResponseEntity.ok(entries);
    }

    // [6] 일기에 태그 연결 (선택적)
    @PostMapping("/entry/{entryId}/tags")
    public ResponseEntity<Map<String, String>> linkTagsToEntry(
            @PathVariable Long entryId,
            @Valid @RequestBody EntryTagsRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        tagService.linkTagsToEntry(entryId, request, userDetails.getMember());
        return ResponseEntity.ok(Map.of("message", "연결 완료"));
    }

    // [7] 일기에서 태그 제거
    @DeleteMapping("/entry/{entryId}/tags/{tagId}")
    public ResponseEntity<Map<String, String>> removeTagFromEntry(
            @PathVariable Long entryId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        tagService.removeTagFromEntry(entryId, tagId, userDetails.getMember());
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }

    // Helper: 로그인 유저 Member 엔티티 조회
    private Member getMember(CustomUserDetails userDetails) {
        return memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 정보 없음"));
    }
}
