package com.diary.domain.tag.service;

import com.diary.domain.entry.dto.EntryResponseDTO;
import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.member.entity.Member;
import com.diary.domain.tag.dto.EntryTagsRequest;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.entity.Tag;
import com.diary.domain.tag.repository.TagRepository;

import com.diary.global.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final DiaryEntryRepository diaryEntryRepository;

    // 로그인한 유저 기준으로 태그 조회
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags(Member member) {
        return tagRepository.findAll().stream()
                .filter(tag -> tag.getMember().equals(member))
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse createTag(TagRequest request, Member member) {
        try {
            Tag tag = tagRepository.findByNameAndMember(request.getName(), member)
                    .orElseGet(() -> tagRepository.save(new Tag(request.getName(), member)));
            return new TagResponse(tag.getId(), tag.getName());
        } catch (DataIntegrityViolationException e) {
            // 중복 삽입을 막기 위한 예외 캐치 (동시성 등)
            Tag tag = tagRepository.findByNameAndMember(request.getName(), member)
                    .orElseThrow(() -> new CustomException("중복 태그 오류", HttpStatus.CONFLICT));
            return new TagResponse(tag.getId(), tag.getName());
        }
    }


    @Transactional
    public TagResponse updateTag(Long tagId, TagRequest request, Member member) {
        Tag tag = tagRepository.findById(tagId)
                .filter(t -> t.getMember().equals(member))
                .orElseThrow(() -> new EntityNotFoundException("태그 없음 또는 권한 없음"));
        tag.setName(request.getName());
        return new TagResponse(tag.getId(), tag.getName());
    }

    @Transactional
    public void deleteTag(Long tagId, Member member) {
        Tag tag = tagRepository.findById(tagId)
                .filter(t -> t.getMember().equals(member))
                .orElseThrow(() -> new EntityNotFoundException("태그 없음 또는 권한 없음"));

        tag.getEntries().forEach(entry -> entry.getTags().remove(tag));
        tag.getEntries().clear();
        tagRepository.delete(tag);
    }

    @Transactional(readOnly = true)
    public List<EntryResponseDTO> getEntriesByTag(Long tagId, Member member) {
        Tag tag = tagRepository.findById(tagId)
                .filter(t -> t.getMember().equals(member))
                .orElseThrow(() -> new EntityNotFoundException("해당 태그 없음 또는 권한 없음"));

        return tag.getEntries().stream()
                .map(this::toEntryResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkTagsToEntry(Long entryId, EntryTagsRequest request, Member member) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("일기 번호 없음 : " + entryId));

        // 권한 검사 (선택)
        if (!entry.getAuthor().getId().equals(member.getId())) {
            throw new CustomException("작성자가 아니므로 태그 연결 불가", HttpStatus.FORBIDDEN);
        }

        List<Tag> tags = request.getTagIds().stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("태그 없음: " + id)))
                .collect(Collectors.toList());

        entry.getTags().addAll(tags);
        diaryEntryRepository.save(entry);
    }

    @Transactional
    public void removeTagFromEntry(Long entryId, Long tagId, Member member) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("일기 번호 없음 : " + entryId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("태그 없음: " + tagId));

        // 권한 검사
        if (!entry.getAuthor().getId().equals(member.getId())) {
            throw new CustomException("작성자가 아니므로 태그 삭제 불가", HttpStatus.FORBIDDEN);
        }

        entry.getTags().remove(tag);
        tag.getEntries().remove(entry);

        if (tag.getEntries().isEmpty() && tag.getMember().getId().equals(member.getId())) {
            tagRepository.delete(tag);
        }

        diaryEntryRepository.save(entry);
    }



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
