// package com.diary;

// import com.diary.domain.diary.dto.DiaryCreateRequest;
// import com.diary.domain.diary.dto.DiaryResponse;
// import com.diary.domain.diary.entity.Diary;
// import com.diary.domain.diary.repository.DiaryRepository;
// import com.diary.domain.diary.service.DiaryService;
// import com.diary.domain.member.entity.Member;
// import com.diary.domain.member.repository.MemberRepository;
// import com.diary.global.exception.CustomException;

// import jakarta.transaction.Transactional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.HttpStatus;

// import static org.assertj.core.api.Assertions.*;

// import java.util.List;

// @SpringBootTest
// @Transactional
// class DearUsDiaryServiceTest {

//     @Autowired
//     private DiaryService diaryService;

//     @Autowired
//     private DiaryRepository diaryRepository;

//     @Autowired
//     private MemberRepository memberRepository;

//     private Long memberId;

//     @BeforeEach
//     void setUp() {
//         Member member = Member.builder()
//                 .userId("testuser")
//                 .nickname("테스트유저")
//                 .password("encodedPassword") // 실제 테스트에선 인코딩된 비밀번호 또는 더미 가능
//                 .build();

//         memberRepository.save(member);
//         memberId = member.getId(); // 필드에 저장
//     }

//     @Test
//     void 다이어리_생성_성공() {
//         // Given
//         DiaryCreateRequest request = new DiaryCreateRequest();
//         request.setName("나의 첫 일기장");

//         // When
//         DiaryResponse response = diaryService.createDiary(memberId, request);

//         // Then
//         assertThat(response.getName()).isEqualTo("나의 첫 일기장");
//         assertThat(response.getOwnerId()).isEqualTo(memberId);
//     }

    @Test
    void 다이어리_목록조회_성공() {
        // Given: 다이어리 직접 저장
        Diary diary = Diary.builder()
                .name("목록용 다이어리")
                .ownerId(memberId)
                .isDeleted(false)
                .build();
        diaryRepository.save(diary);

//         // When
//         List<DiaryResponse> list = diaryService.getDiaryList(memberId);

//         // Then
//         assertThat(list).isNotEmpty();
//         assertThat(list.get(0).getName()).isEqualTo("목록용 다이어리");
//         assertThat(list.get(0).getOwnerId()).isEqualTo(memberId);
//     }
// }
