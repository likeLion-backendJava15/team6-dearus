package com.diary.domain.comment.controller;

import com.diary.domain.comment.dto.CommentCreateRequest;
import com.diary.domain.comment.dto.CommentResponse;
import com.diary.domain.comment.dto.CommentUpdateRequest;
import com.diary.domain.comment.service.CommentService;
import com.diary.domain.member.security.MemberDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entry/{entryId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long entryId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        CommentResponse response = commentService.createComment(entryId, request, memberDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long entryId) {

        List<CommentResponse> comments = commentService.getComments(entryId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        CommentResponse updated = commentService.updateComment(commentId, request, memberDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        commentService.deleteComment(commentId, memberDetails);
        return ResponseEntity.noContent().build();
    }
}