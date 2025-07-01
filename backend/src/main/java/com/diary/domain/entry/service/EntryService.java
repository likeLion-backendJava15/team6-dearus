package com.diary.domain.entry.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.util.Set;

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
import com.diary.global.util.EmotionUtils;
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

    private DiaryEntry getEntryOrThrow(Long entryId) {
        return diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new CustomException("일기를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    // 일기 등록
    public Long createEntry(EntryCreateRequestDTO requestDTO, Long authorId) throws IOException {
        Member author = getMemberOrThrow(authorId);
        Diary diary = getDiaryOrThrow(requestDTO.getDiaryId());

        String content = requestDTO.getContent();
        // 이미지 URL 파싱 및 이동
        List<String> imageUrls = HtmlImageParser.extractAllImageUrls(content);
        String firstResolvedImageUrl = null;

        for (String url : imageUrls) {
            if (url.startsWith("/temp-uploads/")) {
                String filename = url.substring("/temp-uploads/".length());

                Path source = Paths.get(System.getProperty("user.dir") + "/temp-uploads/" + filename);
                Path target = Paths.get(System.getProperty("user.dir") + "/uploads/" + filename);

                System.out.println("tempPath 존재 여부: " + Files.exists(source));
                System.out.println("tempPath 절대 경로: " + source.toAbsolutePath());

                Files.createDirectories(target.getParent());
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

                String newUrl = "/uploads/" + filename;
                content = content.replace(url, "/uploads/" + filename);

                if (firstResolvedImageUrl == null) {
                    firstResolvedImageUrl = newUrl;
                }
            } else {
                // ✅ 업로드된 이미지가 아닌 외부 이미지 등도 처리 가능
                if (firstResolvedImageUrl == null) {
                    firstResolvedImageUrl = url;
                }
            }
        }

        // 대표 이미지 URL 결정
        String resolvedImageUrl = firstResolvedImageUrl;

        // 엔트리 생성
        DiaryEntry entry = DiaryEntry.builder()
                .diary(diary)
                .author(author)
                .title(requestDTO.getTitle())
                .content(content) // ✅ 수정된 content 사용
                .imageUrl(resolvedImageUrl)
                .emotion(requestDTO.getEmotion())
                .build();

        // 태그 처리
        if (requestDTO.getTags() != null) {
            for (String tagName : requestDTO.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                entry.getTags().add(tag);
            }
        }

        diaryEntryRepository.save(entry);
        deleteUnusedTempImages(imageUrls);

        return entry.getId();
    }

    private void deleteUnusedTempImages(List<String> usedUrls) {
        File tempDir = new File(System.getProperty("user.dir") + "/temp-uploads");
        if (!tempDir.exists()) {
            return;
        }

        File[] files = tempDir.listFiles();
        if (files == null) {
            return;
        }

        Set<String> usedFilenames = usedUrls.stream()
                .filter(url -> url.startsWith("/temp-uploads/"))
                .map(url -> url.substring("/temp-uploads/".length()))
                .collect(Collectors.toSet());

        for (File file : files) {
            if (!usedFilenames.contains(file.getName())) {
                file.delete();
            }
        }
    }

    // 일기 목록 조회
    public List<EntryListResponseDTO> getAllEntriesByDiaryId(Long diaryId) {

        List<DiaryEntry> entries = diaryEntryRepository.findByDiaryIdOrderByCreatedAtDesc(diaryId);

        return entries.stream()
                .map(entry -> {
                    List<String> tagNames = entry.getTags().stream()
                            .map(tag -> tag.getName())
                            .collect(Collectors.toList());

                    return EntryListResponseDTO.builder()
                            .id(entry.getId())
                            .diaryId(entry.getDiary().getId())
                            .authorId(entry.getAuthor().getId())
                            .title(entry.getTitle())
                            .emotion(entry.getEmotion())
                            .imageUrl(entry.getImageUrl())
                            .authorNickname(entry.getAuthor().getNickname())
                            .createdAt(entry.getCreatedAt())
                            .emotionEmoji(EmotionUtils.toEmoji(entry.getEmotion()))
                            .tags(tagNames)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 일기 상세 조회
    @Transactional(readOnly = true)
    public EntryResponseDTO getEntryDetail(Long entryId) {
        DiaryEntry entry = getEntryOrThrow(entryId);

        List<String> tagNames = entry.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

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
                .emotionEmoji(EmotionUtils.toEmoji(entry.getEmotion()))
                .tags(tagNames)
                .build();
    }

    // 일기 수정
    public void updateEntry(Long entryId, EntryUpdateRequestDTO requestDto, Long memberId) {
        DiaryEntry entry = getEntryOrThrow(entryId);

        if (!entry.getAuthor().getId().equals(memberId)) {
            throw new CustomException("작성자가 아니므로 수정 불가", HttpStatus.FORBIDDEN);
        }

        // 대표 이미지 처리
        String content = requestDto.getContent();
        List<String> oldImageUrls = HtmlImageParser.extractAllImageUrls(entry.getContent());
        List<String> imageUrls = HtmlImageParser.extractAllImageUrls(content);
        String firstResolvedImageUrl = null;

        for (String url : imageUrls) {
            if (url.startsWith("/temp-uploads/")) {
                String filename = url.substring("/temp-uploads/".length());
                Path source = Paths.get(System.getProperty("user.dir") + "/temp-uploads/" + filename);
                Path target = Paths.get(System.getProperty("user.dir") + "/uploads/" + filename);

                try {
                    Files.createDirectories(target.getParent());
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                content = content.replace(url, "/uploads/" + filename);

                if (firstResolvedImageUrl == null) {
                    firstResolvedImageUrl = "/uploads/" + filename;
                }
            } else {
                if (firstResolvedImageUrl == null) {
                    firstResolvedImageUrl = url;
                }
            }
        }

        String resolvedImageUrl = (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isBlank())
                ? requestDto.getImageUrl()
                : firstResolvedImageUrl;

        entry.update(
                requestDto.getTitle(),
                content,
                resolvedImageUrl,
                requestDto.getEmotion());

        deleteUnusedTempImages(imageUrls);

        Set<String> newUploadImageFilenames = imageUrls.stream()
                .filter(url -> url.startsWith("/uploads/"))
                .map(url -> url.substring("/uploads/".length()))
                .collect(Collectors.toSet());

        for (String url : oldImageUrls) {
            if (url.startsWith("/uploads/")) {
                String filename = url.substring("/uploads/".length());
                if (!newUploadImageFilenames.contains(filename)) {
                    File file = new File(System.getProperty("user.dir") + "/uploads/" + filename);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }

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
