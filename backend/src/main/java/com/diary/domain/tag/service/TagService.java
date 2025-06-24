package com.diary.domain.tag.service;

import com.diary.domain.entry.entity.DiaryEntry;
import com.diary.domain.entry.repository.DiaryEntryRepository;
import com.diary.domain.tag.dto.EntryTagsRequest;
import com.diary.domain.tag.dto.TagRequest;
import com.diary.domain.tag.dto.TagResponse;
import com.diary.domain.tag.entity.Tag;
import com.diary.domain.tag.repository.TagRepository;
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

    @Transactional
    public TagResponse createTag(TagRequest request) {
        Optional<Tag> existingTag = tagRepository.findByName(request.getName());

        if (existingTag.isPresent()) {
            Tag tag = existingTag.get();
            return new TagResponse(tag.getId(), tag.getName());
        }

        Tag tag = new Tag();
        tag.setName(request.getName());

        Tag savedTag = tagRepository.save(tag);

        return new TagResponse(savedTag.getId(), savedTag.getName());
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkTagsToEntry(Integer entryId, EntryTagsRequest request) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("일기 번호 없음 : " + entryId));

        List<Tag> tags = tagRepository.findAllById(request.getTagIds());

        // Add all tags to the entry
        Set<Tag> entryTags = entry.getTags();
        entryTags.addAll(tags);

        diaryEntryRepository.save(entry);
    }

    @Transactional
    public void removeTagFromEntry(Integer entryId, Integer tagId) {
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("일기 번호 없음 : " + entryId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("태그 번호 없음 : " + tagId));

        // Remove the tag from the entry
        entry.getTags().remove(tag);

        diaryEntryRepository.save(entry);
    }
}
