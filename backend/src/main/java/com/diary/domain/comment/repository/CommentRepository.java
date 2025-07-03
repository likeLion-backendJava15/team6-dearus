package com.diary.domain.comment.repository;

import com.diary.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM comment WHERE entry_id = :entryId ORDER BY id", nativeQuery = true)
    List<Comment> findByDiaryEntryId(@Param("entryId") Long entryId);

    List<Comment> findByParentCommentId(Long parentCommentId);
}
