package com.diary.domain.entry.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.entry.dto.EntryCreateRequestDTO;
import com.diary.domain.entry.dto.EntryListResponseDto;
import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.dto.EntryUpdateRequestDTO;
import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;
import com.diary.global.exception.CustomException;

@Service
@Transactional
public class EntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    public EntryService(DiaryEntryRepository diaryEntryRepository, DiaryRepository diaryRepository,
            MemberRepository memberRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.diaryRepository = diaryRepository;
        this.memberRepository = memberRepository;
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
        Diary diary = getDiaryOrThrow(authorId);

        DiaryEntry entry = DiaryEntry.builder()
                .diary(diary)
                .author(author)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .imageUrl(requestDTO.getImageUrl())
                .emotion(requestDTO.getEmotion())
                .build();

        return diaryEntryRepository.save(entry).getId();
    }

    // 일기 목록 조회
    public List<EntryListResponseDto> getAllEntriesByDiaryId(Long diaryId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByDiaryIdOrderByCreatedAtDesc(diaryId);
        return entries.stream()
                .map((DiaryEntry entry) -> EntryListResponseDto.builder()
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
