package com.diary.domain.entry.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.diary.domain.entry.entity.DiaryEntry;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
	List<DiaryEntry> findByDiaryIdOrderByCreatedAtDesc(Long diaryId);
}
