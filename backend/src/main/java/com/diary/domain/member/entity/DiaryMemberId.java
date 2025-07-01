package com.diary.domain.member.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
public class DiaryMemberId implements Serializable {

    private Long diaryId;
    private Long userId;
    
}
