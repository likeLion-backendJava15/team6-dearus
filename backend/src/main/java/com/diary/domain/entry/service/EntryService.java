package com.diary.domain.entry.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.entry.dto.EntryCreateRequestDTO;
import com.diary.domain.entry.dto.EntryListResponseDTO;
import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.dto.EntryUpdateRequestDTO;
import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.domain.tag.entity.Tag;
import com.diary.domain.tag.repository.TagRepository;
import com.diary.global.exception.CustomException;
import com.diary.global.util.HtmlImageParser;

@Service
@Transactional
public class EntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;

    public EntryService(DiaryEntryRepository diaryEntryRepository, DiaryRepository diaryRepository,
            MemberRepository memberRepository, TagRepository tagRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.diaryRepository = diaryRepository;
        this.memberRepository = memberRepository;
        this.tagRepository = tagRepository;
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    private Diary getDiaryOrThrow(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    private DiaryEntry getEntryOrThrow(Long diaryId) {
        return diaryEntryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException("일기장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    // 일기 등록
    public Long createEntry(EntryCreateRequestDTO requestDTO, Long authorId) {
        Member author = getMemberOrThrow(authorId);
        Diary diary = getDiaryOrThrow(requestDTO.getDiaryId());

        // 대표 이미지가 비어 있으면 content에서 자동 추출
        String resolvedImageUrl = requestDTO.getImageUrl();
        if (resolvedImageUrl == null || resolvedImageUrl.isBlank()) {
            resolvedImageUrl = HtmlImageParser.extractFirstImageUrl(requestDTO.getContent());
        }

        DiaryEntry entry = DiaryEntry.builder()
                .diary(diary)
                .author(author)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .imageUrl(resolvedImageUrl)
                .emotion(requestDTO.getEmotion())
                .build();

        if (requestDTO.getTags() != null) {
            for (String tagName : requestDTO.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                entry.getTags().add(tag);
            }
        }

        diaryEntryRepository.save(entry);

        return entry.getId();
    }

    // 일기 목록 조회
    public List<EntryListResponseDTO> getAllEntriesByDiaryId(Long diaryId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByDiaryIdOrderByCreatedAtDesc(diaryId);
        return entries.stream()
                .map((DiaryEntry entry) -> EntryListResponseDTO.builder()
                        .id(entry.getId())
                        .title(entry.getTitle())
                        .emotion(entry.getEmotion())
                        .imageUrl(entry.getImageUrl())
                        .authorNickname(entry.getAuthor().getNickname())
                        .createdAt(entry.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 일기 상세 조회
    @Transactional(readOnly = true)
    public EntryResponseDTO getEntryDetail(Long entryId) {
        DiaryEntry entry = getEntryOrThrow(entryId);

        return EntryResponseDTO.builder()
                .id(entry.getId())
                .diaryId(entry.getDiary().getId())
                .authorId(entry.getAuthor().getId())
                .authorNickname(entry.getAuthor().getNickname())
                .title(entry.getTitle())
                .content(entry.getContent())
                .emotion(entry.getEmotion().name())
                .imageUrl(entry.getImageUrl())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }

    // 일기 수정
    public void updateEntry(Long entryId, EntryUpdateRequestDTO requestDto, Long memberId) {
        DiaryEntry entry = getEntryOrThrow(entryId);

        if (!entry.getAuthor().getId().equals(memberId)) {
            throw new CustomException("작성자가 아니므로 수정 불가", HttpStatus.FORBIDDEN);
        }

        entry.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getImageUrl(),
                requestDto.getEmotion());

        // 태그 수정
        if (requestDto.getTags() != null) {
            // 기존 태그 제거
            entry.getTags().clear();

            // 새 태그 리스트
            for (String tagName : requestDto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                entry.getTags().add(tag);
            }
        }
    }

    // 일기 삭제
    public void deleteEntry(Long entryId, Long memberId) {
        DiaryEntry entry = getEntryOrThrow(entryId);

        if (!entry.getAuthor().getId().equals(memberId)) {
            throw new CustomException("작성자가 아니므로 삭제 불가", HttpStatus.FORBIDDEN);
        }

        diaryEntryRepository.delete(entry);
    }

}
