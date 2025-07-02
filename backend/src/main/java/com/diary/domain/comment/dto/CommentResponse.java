package com.diary.domain.comment.dto;

import com.diary.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter 
@Builder
public class CommentResponse {

    private Long id;
    private Long entryId;
    private Long memberId;
    private String memberNickname;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<CommentResponse> children = new ArrayList<>(); 

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .entryId(comment.getDiaryEntry() != null ? comment.getDiaryEntry().getId() : null)
                .memberId(comment.getMember() != null ? comment.getMember().getId() : null)
                .memberNickname(comment.getMember() != null ? comment.getMember().getNickname() : null)
                .content(comment.getContent())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .children(new ArrayList<>())
                .build();
    }
}
