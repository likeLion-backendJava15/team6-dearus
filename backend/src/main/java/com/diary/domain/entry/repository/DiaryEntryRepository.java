package com.diary.domain.entry.repository;

import com.diary.domain.entry.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {

}
