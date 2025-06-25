package com.diary.domain.comment.service;

import com.diary.domain.comment.dto.CommentCreateRequest;
import com.diary.domain.comment.dto.CommentResponse;
import com.diary.domain.comment.dto.CommentUpdateRequest;
import com.diary.domain.comment.entity.Comment;
import com.diary.domain.comment.repository.CommentRepository;
import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.domain.member.security.CustomUserDetails;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryEntryRepository diaryEntryRepository;
    private final MemberRepository memberRepository;

    public CommentResponse createComment(Long entryId, CommentCreateRequest request, CustomUserDetails userDetails) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("일기 항목을 찾을 수 없습니다."));

        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .diaryEntry(entry)
                .member(member)
                .content(request.getContent())
                .parentComment(request.getParentCommentId() != null ?
                        commentRepository.findById(request.getParentCommentId()).orElse(null) : null)
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    public List<CommentResponse> getComments(Long entryId) {
        return commentRepository.findByDiaryEntryId(entryId).stream()
                .map(CommentResponse::from)
                .collect(toList());
    }

    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getMemberId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    public void deleteComment(Long commentId, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getMemberId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.deleteById(commentId);
    }
}
