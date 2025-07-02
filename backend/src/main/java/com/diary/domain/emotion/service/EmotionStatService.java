package com.diary.domain.emotion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.emotion.dto.EmotionCountDTO;
import com.diary.domain.emotion.dto.EmotionStatResponse;
import com.diary.domain.emotion.dto.UserEmotionCountDTO;
import com.diary.domain.emotion.repository.EmotionStatRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmotionStatService {

    private final EmotionStatRepositoryCustom emotionStatRepository;

    @Transactional(readOnly = true)
    public EmotionStatResponse getEmotionStatistics(Long diaryId, Long userId, LocalDateTime start, LocalDateTime end) {
        EmotionStatResponse response = emotionStatRepository.getEmotionStatResponse(diaryId, userId, start, end);

        List<EmotionCountDTO> totalCounts = response.getTotalcounts();
        Long total = totalCounts.stream().mapToLong(EmotionCountDTO::getCount).sum();

        if (total > 0) {
            for (EmotionCountDTO dto : totalCounts) {
                double percent = (dto.getCount() * 100.0) / total;
                dto.setPercent(Math.round(percent * 10.0) / 10.0);
            }
        }

        response.getUsercounts().forEach(user -> {
            long userTotal = user.getEmotionCounts().stream().mapToLong(EmotionCountDTO::getCount).sum();
            if (userTotal > 0) {
                for (EmotionCountDTO dto : user.getEmotionCounts()) {
                    double percent = (dto.getCount() * 100.0) / userTotal;
                    dto.setPercent(Math.round(percent * 10.0) / 10.0);
                }
            }
        });

        UserEmotionCountDTO selectedUser = response.getSelectedUserCount();
        if (selectedUser != null) {
            long selectedUserTotal = selectedUser.getEmotionCounts().stream()
                    .mapToLong(EmotionCountDTO::getCount).sum();
            if (selectedUserTotal > 0) {
                for (EmotionCountDTO dto : selectedUser.getEmotionCounts()) {
                    double percent = (dto.getCount() * 100.0) / selectedUserTotal;
                    dto.setPercent(Math.round(percent * 10.0) / 10.0);
                }
            }
        }

        return response;

    }

}
