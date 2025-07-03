package com.diary.domain.comment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.comment.dto.CommentCreateRequest;
import com.diary.domain.comment.dto.CommentResponse;
import com.diary.domain.comment.dto.CommentUpdateRequest;
import com.diary.domain.comment.entity.Comment;
import com.diary.domain.comment.repository.CommentRepository;
import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.global.auth.CustomUserDetails;
import com.diary.global.exception.CustomException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryEntryRepository diaryEntryRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    private DiaryEntry getEntryOrThrow(Long entryId) {
        return diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new CustomException("일기를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public CommentResponse createComment(Long entryId, CommentCreateRequest request, CustomUserDetails userDetails) {
        DiaryEntry entry = getEntryOrThrow(entryId);

        Member member = getMemberOrThrow(userDetails.getId());

        Comment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글이 존재하지 않습니다."));
        }

        Comment comment = Comment.builder()
                .diaryEntry(entry)
                .member(member)
                .content(request.getContent())
                .parentComment(parent)
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentResponse> getComments(Long entryId) {
        
        List<Comment> comments = commentRepository.findByDiaryEntryId(entryId);

        // 엔티티 → DTO
        Map<Long, CommentResponse> commentMap = new HashMap<>();
        for (Comment comment : comments) {
            commentMap.put(comment.getId(), CommentResponse.from(comment));
        }

        // 트리 구조 생성
        List<CommentResponse> roots = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse current = commentMap.get(comment.getId());
            if (comment.getParentComment() != null) {
                CommentResponse parent = commentMap.get(comment.getParentComment().getId());
                if (parent != null) {
                    parent.getChildren().add(current);
                }
            } else {
                roots.add(current);
            }
        }

        return roots;
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        System.out.println("작성자: " + comment.getMember().getUserId());
        System.out.println("현재 로그인 사용자: " + userDetails.getUsername());

        if (!comment.getMember().getUserId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
        }
        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getUserId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }
        commentRepository.deleteById(commentId);
    }
}
