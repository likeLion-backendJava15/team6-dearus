package com.diary.domain.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diary.domain.comment.dto.CommentCreateRequest;
import com.diary.domain.comment.dto.CommentResponse;
import com.diary.domain.comment.dto.CommentUpdateRequest;
import com.diary.domain.comment.service.CommentService;
import com.diary.global.auth.CustomUserDetails;

@RestController
@RequestMapping("/api/entry/{entryId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long entryId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponse response = commentService.createComment(entryId, request, userDetails);
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
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponse updated = commentService.updateComment(commentId, request, userDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long entryId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        System.out.println("삭제 요청: entryId=" + entryId + ", commentId=" + commentId);
        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.noContent().build();
    }
}