package com.diary;

import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.diary.service.DiaryService;
import com.diary.global.exception.CustomException;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Transactional
public class DearUsDiaryServiceTest {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void getDiaryList_정상조회() {
        // given
        diaryRepository.save(Diary.builder().name("일기장1").build());
        diaryRepository.save(Diary.builder().name("일기장2").build());

        // when
        List<DiaryResponse> result = diaryService.getDiaryList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("일기장1");
    }

    @Test
    void getDiary_정상조회() {
        // given
        Diary diary = diaryRepository.save(Diary.builder().name("테스트일기").build());

        // when
        DiaryResponse result = diaryService.getDiary(diary.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트일기");
    }

    @Test
    void getDiary_삭제된일기_예외발생() {
        // given
        Diary diary = diaryRepository.save(Diary.builder().name("삭제일기").deleted(true).build());

        // when & then
        assertThatThrownBy(() -> diaryService.getDiary(diary.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("존재하지 않거나 삭제된 일기장입니다.");
    }
}