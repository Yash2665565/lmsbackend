package com.school.content.repository;

import com.school.content.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(l.title) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Lesson> search(@Param("q") String q, Pageable pageable);

    @Query(value = "SELECT l.* FROM lessons l JOIN lesson_topic lt ON lt.lesson_id = l.id WHERE lt.topic_id = :topicId", nativeQuery = true)
    List<Lesson> findByTopicId(@Param("topicId") Long topicId);
}
