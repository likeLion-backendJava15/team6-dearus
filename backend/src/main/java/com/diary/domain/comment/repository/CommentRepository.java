package com.diary.domain.comment.repository;

import com.diary.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByDiaryEntryId(Long entryId);

    List<Comment> findByParentCommentId(Long parentCommentId);
}