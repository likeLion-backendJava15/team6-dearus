package com.diary.domain.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    private Long parentCommentId;
    private Long memberId;
    private String content;
}