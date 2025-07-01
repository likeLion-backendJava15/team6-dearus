package com.diary.domain.diary.service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.global.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

        private final DiaryRepository diaryRepository;

        // 다이어리 생성 서비스 : 예외처리 완료
        public DiaryResponse createDiary(Long memberId, DiaryCreateRequest requestDto) {
                // 예외처리 : 이름 공백
                if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
                        throw new CustomException("일기장 이름은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
                }

                Diary diary = Diary.builder()
                                .name(requestDto.getName())
                                .ownerId(memberId) // FK로 memberId 저장
                                .isDeleted(false)
                                .build();


                diaryRepository.save(diary);

                return DiaryResponse.builder()
                                .id(diary.getId())
                                .name(diary.getName())
                                .ownerId(diary.getOwnerId())
                                .build();
        }

        // 다이어리 목록 조회 : 소유권 없이도 전체 목록 조회
        @Transactional(readOnly = true)
        public List<DiaryResponse> getDiaryList(Long memberId) {

                return diaryRepository.findAllByOwnerIdAndIsDeletedFalse(memberId)
                                .stream()
                                .map(diary -> DiaryResponse.builder()
                                                .id(diary.getId())
                                                .name(diary.getName())
                                                .ownerId(diary.getOwnerId())
                                                .build())
                                .collect(Collectors.toList());
        }

        // 다이어리 단일 조회 : 소유권이 없는 유저가 요청 시 비어 있을 경우 404 예외처리 완료
        @Transactional(readOnly = true)
        public DiaryResponse getDiary(Long diaryId, Long memberId) {
                Diary diary = diaryRepository.findById(diaryId)
                                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND)); // 404
                                                                                                                  // 일기장
                                                                                                                  // 없음.

                return DiaryResponse.builder()
                                .id(diary.getId())
                                .name(diary.getName())
                                .ownerId(diary.getOwnerId())
                                .build();
        }

        // 다이어리 수정 요청 : 권한 403 예외처리 완료
        public DiaryResponse updateDiary(Long diaryId, Long memberId, DiaryUpdateRequest requestDto) {
                Diary diary = diaryRepository.findById(diaryId)
                                .orElseThrow(() -> new IllegalArgumentException("일기장이 존재하지 않습니다."));

                if (!diary.getOwnerId().equals(memberId)) {
                        throw new CustomException("해당 일기장에 대해 권한이 없습니다.", HttpStatus.FORBIDDEN);
                }

                Diary updatedDiary = Diary.builder()
                                .id(diary.getId())
                                .name(requestDto.getName())
                                .ownerId(diary.getOwnerId()) // FK 유지
                                .isDeleted(diary.getIsDeleted())
                                .build();

                diaryRepository.save(updatedDiary);

                return DiaryResponse.builder()
                                .id(updatedDiary.getId())
                                .name(updatedDiary.getName())
                                .ownerId(updatedDiary.getOwnerId())
                                .build();
        }

        // 다이어리 삭제 요청 :
        @Transactional
        public void deleteDiary(Long diaryId, Long memberId) {
                // 1) 다이어리 조회 + 404 예외
                Diary diary = diaryRepository.findById(diaryId)
                                .orElseThrow(() -> new CustomException("일기장이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

                // 2) 소유자 권한 확인
                if (!diary.getOwnerId().equals(memberId)) {
                        throw new CustomException("해당 일기장을 삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN);
                }

                // 3) 연관 엔트리 물리 삭제 | 연관 엔트리 Soft Delete 처리 -> entry isDelete?
                if (diary.getEntries() != null && !diary.getEntries().isEmpty()) {
                        // diary.getEntries()는 orphanRemoval = true 이므로,
                        // 엔트리를 비워주면 JPA가 DELETE 쿼리 날림
                        diary.getEntries().clear();
                }

                // 4) 다이어리 자체도 Soft Delete
                diary.setIsDeleted(true);

                // JPA 변경 감지로 save() 호출 안 해도 됨 → 안전을 위해 명시적 save() 호출도 OK
                diaryRepository.save(diary);
        }
}