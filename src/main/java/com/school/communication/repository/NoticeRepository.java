package com.school.communication.repository;

import com.school.communication.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByTargetTypeOrderByCreatedAtDesc(String targetType);

    List<Notice> findAllByOrderByCreatedAtDesc();
}
