package com.school.lms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_notes")
@Getter @Setter
public class CourseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    @Column(name = "note_type")
    @Enumerated(EnumType.STRING)
    private NoteType noteType = NoteType.TEXT;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum NoteType { TEXT, PDF, VIDEO, LINK }
}
