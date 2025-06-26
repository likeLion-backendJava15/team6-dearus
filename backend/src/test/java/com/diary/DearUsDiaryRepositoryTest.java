package com.diary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DearUsDiaryRepositoryTest {

    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void diarySaveTest() {
        Diary diary = Diary.builder().name("테스트 다이어리").build();
        Diary saved = diaryRepository.save(diary);

        assertThat(saved.getId()).isNotNull();
    }
}
