package com.diary;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.diary.service.DiaryService;
import com.diary.domain.member.entity.DiaryMember;
import com.diary.domain.member.entity.User;
import com.diary.domain.member.service.MemberService;
import com.diary.domain.member.type.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DearUsDiaryServiceTest {

    @InjectMocks
    private DiaryService diaryService;

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private MemberService memberService;

    private DiaryMember mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockUser = DiaryMember.builder()
                .id(1L)
                .userId("testUser")
                .nickname("Tester")
                .password("pw")
                .build();
    }

    @Test
    void createDiary_정상_생성_및_멤버추가() {
        // given
        DiaryCreateRequest dto = DiaryCreateRequest.builder().name("감성일기").build();

        Diary savedDiary = Diary.builder()
                .id(100L)
                .name("감성일기")
                .build();

        when(memberService.getCurrentUser()).thenReturn(mockUser);
        when(diaryRepository.save(any(Diary.class))).thenReturn(savedDiary);

        // when
        DiaryResponse response = diaryService.createDiary(dto);

        // then
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("감성일기");

        verify(memberService).addMemberToDiary(mockUser, savedDiary, Role.OWNER);
    }

    @Test
    void getMyDiaries_내_일기장_조회() {
        // given
        when(memberService.getCurrentUser()).thenReturn(mockUser);

        Diary diary1 = Diary.builder().id(1L).name("다이어리1").build();
        Diary diary2 = Diary.builder().id(2L).name("다이어리2").build();

        when(memberService.findDiariesByUser(mockUser)).thenReturn(List.of(diary1, diary2));

        // when
        List<DiaryResponse> result = diaryService.getMyDiaries();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("다이어리1");
        assertThat(result.get(1).getName()).isEqualTo("다이어리2");
    }
}
