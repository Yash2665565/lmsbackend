package com.school.communication.service;

import com.school.communication.dto.NoticeCreateRequest;
import com.school.communication.dto.NoticeDto;
import com.school.communication.entity.Notice;
import com.school.communication.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public List<NoticeDto> listAll() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public NoticeDto create(NoticeCreateRequest req) {
        Notice notice = new Notice();
        notice.setName(req.getName());
        notice.setContent(req.getContent());
        notice.setTargetType(req.getTargetType());
        notice.setTargetId(req.getTargetId());
        notice.setMandatory(req.getMandatory());
        notice.setCreatedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());
        return toDto(noticeRepository.save(notice));
    }

    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────

    private NoticeDto toDto(Notice n) {
        return NoticeDto.builder()
                .id(n.getId())
                .name(n.getName())
                .content(n.getContent())
                .mandatory(n.getMandatory())
                .targetType(n.getTargetType())
                .targetId(n.getTargetId())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
