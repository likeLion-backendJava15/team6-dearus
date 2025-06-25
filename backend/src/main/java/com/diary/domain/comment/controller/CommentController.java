package com.diary.domain.comment.controller;

import com.diary.domain.comment.dto.CommentCreateRequest;
import com.diary.domain.comment.dto.CommentResponse;
import com.diary.domain.comment.dto.CommentUpdateRequest;
import com.diary.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails) {

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
            @AuthenticationPrincipal UserDetails userDetails) {

        CommentResponse updated = commentService.updateComment(commentId, request, userDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.noContent().build();
    }
}