package com.diary.domain.diary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diary.domain.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByOwnerIdAndIsDeletedFalse(Long id);
}
