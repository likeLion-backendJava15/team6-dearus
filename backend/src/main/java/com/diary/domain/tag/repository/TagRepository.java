package com.diary.domain.tag.repository;

import com.diary.domain.member.entity.Member;
import com.diary.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name); // 태그 이름으로 태그 찾는 메서드
    Optional<Tag> findByNameAndMember(String name, Member member);
    List<Tag> findAllByMember(Member member);
}
