package com.diary.domain.diary.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.diary.domain.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
