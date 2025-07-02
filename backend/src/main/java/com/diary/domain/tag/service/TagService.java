package com.diary.domain.tag.service;

import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.tag.dto.EntryTagsRequest;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.entity.Tag;
import com.diary.domain.tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final DiaryEntryRepository diaryEntryRepository;

    public TagService(TagRepository tagRepository, DiaryEntryRepository diaryEntryRepository) {
        this.tagRepository = tagRepository;
        this.diaryEntryRepository = diaryEntryRepository;
    }

    // 1. 태그 생성
    @Transactional
    public TagResponse createTag(TagRequest request) {
        // 이미 태그가 존재하는지 확인
        Optional<Tag> existingTag = tagRepository.findByName(request.getName());

        if (existingTag.isPresent()) {
            // true -> 기존 태그 반환
            Tag tag = existingTag.get();
            return new TagResponse(tag.getId(), tag.getName());
        }
        // 새 태그 생성
        Tag tag = new Tag();
        tag.setName(request.getName());

        Tag savedTag = tagRepository.save(tag);

        return new TagResponse(savedTag.getId(), savedTag.getName());
    }

    // 2) 전체 태그 조회
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        // 모든 태그 조회 후 DTO 변환 반환
        return tagRepository.findAll().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    // 3) 태그 수정
    @Transactional
    public TagResponse updateTag(Long tagId, TagRequest request) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("태그 번호 없음: " + tagId));
        tag.setName(request.getName());
        Tag saved = tagRepository.save(tag);
        return new TagResponse(saved.getId(), saved.getName());
    }

    // 4) 태그 삭제
    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("태그 번호 없음: " + tagId));

        // 연결된 일기들에서 이 태그를 제거
        tag.getEntries().forEach(entry -> entry.getTags().remove(tag));
        tag.getEntries().clear(); // 양방향 해제

        tagRepository.delete(tag);

    }


    // 5) 태그별 일기 조회
    @Transactional(readOnly = true)
    public List<EntryResponseDTO> getEntriesByTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + tagId));
        return tag.getEntries()
                .stream()
                .map(this::toEntryResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkTagsToEntry(Long entryId, EntryTagsRequest request) {
        // 일기 조회
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("일기 번호 없음 : " + entryId));
        // 태그 ID 목록으로 태그들 조회
        List<Tag> tags = tagRepository.findAllById(request.getTagIds());

        // 일기에 태그 추가
        Set<Tag> entryTags = entry.getTags();
        entryTags.addAll(tags);

        diaryEntryRepository.save(entry);
    }

    @Transactional
    public void removeTagFromEntry(Long entryId, Long tagId) {
        // 일기 조회
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("일기 번호 없음 : " + entryId));
        // 태그 조회
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("태그 번호 없음 : " + tagId));

        // 태그 제거
        entry.getTags().remove(tag);

        diaryEntryRepository.save(entry);
    }

    // Helper: DiaryEntry → EntryResponse
    private EntryResponseDTO toEntryResponseDTO(DiaryEntry e) {
        return EntryResponseDTO.builder()
                .id(e.getId())
                .diaryId(e.getDiary().getId())
                .authorId(e.getAuthor().getId())
                .authorNickname(e.getAuthor().getNickname())
                .title(e.getTitle())
                .content(e.getContent())
                .emotion(e.getEmotion().name())
                .imageUrl(e.getImageUrl())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
