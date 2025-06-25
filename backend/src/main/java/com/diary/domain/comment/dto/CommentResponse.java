package com.diary.domain.comment.dto;

import com.diary.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private Long entryId;
    private Long userId;
    private String nickname;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .entryId(comment.getDiaryEntry() != null ? comment.getDiaryEntry().getId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .nickname(comment.getUser() != null ? comment.getUser().getNickname() : null)
                .content(comment.getContent())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}