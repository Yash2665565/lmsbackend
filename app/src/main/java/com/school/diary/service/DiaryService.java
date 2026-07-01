package com.school.diary.service;

import com.school.diary.dto.DiaryCreateRequest;
import com.school.diary.dto.DiaryDto;
import com.school.diary.entity.Diary;
import com.school.diary.entity.DiaryResponse;
import com.school.diary.repository.DiaryRepository;
import com.school.diary.repository.DiaryRepository.DiaryView;
import com.school.diary.repository.DiaryRepository.SectionLite;
import com.school.diary.repository.DiaryRepository.SectionTeacherRow;
import com.school.diary.repository.DiaryResponseRepository;
import com.school.diary.repository.DiaryResponseRepository.RosterRow;
import com.school.identity.entity.User;
import com.school.identity.repository.StudentRepository;
import com.school.identity.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepo;
    private final TeacherRepository teacherRepo;
    private final DiaryResponseRepository responseRepo;
    private final StudentRepository studentRepo;

    private boolean isAdmin(User user) {
        for (GrantedAuthority a : user.getAuthorities()) {
            String r = a.getAuthority().toUpperCase();
            if (r.contains("ADMIN")) return true;
        }
        return false;
    }

    private Long teacherIdOf(User user) {
        return teacherRepo.findByUserId(user.getId()).map(t -> t.getId()).orElse(null);
    }

    private DiaryDto toDto(DiaryView v) {
        return DiaryDto.builder()
                .id(v.getId()).title(v.getTitle()).description(v.getDescription())
                .dueDate(v.getDueDate())
                .subjectId(v.getSubjectId()).subjectName(v.getSubjectName())
                .sectionId(v.getSectionId())
                .teacherId(v.getTeacherId()).teacherName(v.getTeacherName())
                .createdAt(v.getCreatedAt())
                .build();
    }

    public List<DiaryDto> listForSection(Long sectionId) {
        return diaryRepo.findEnrichedBySection(sectionId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<DiaryDto> studentDiary(Long studentId) {
        Long sectionId = diaryRepo.findSectionIdByStudent(studentId);
        if (sectionId == null) return List.of();
        List<DiaryDto> entries = listForSection(sectionId);
        Map<Long, DiaryResponse> mine = responseRepo.findByStudentId(studentId).stream()
                .collect(Collectors.toMap(DiaryResponse::getDiaryId, r -> r, (a, b) -> a));
        for (DiaryDto e : entries) {
            DiaryResponse r = mine.get(e.getId());
            if (r != null) {
                e.setMyStatus(r.getStatus());
                e.setMyNote(r.getNote());
            }
        }
        return entries;
    }

    /** Student marks a diary entry done / not-done and/or leaves a note. Resolved from the principal. */
    @Transactional
    public Map<String, Object> respond(User user, Long diaryId, String status, String note) {
        Long studentId = studentRepo.findByUserId(user.getId()).map(s -> s.getId()).orElse(null);
        if (studentId == null) throw new RuntimeException("Only a student can respond to diary entries.");
        Diary d = diaryRepo.findById(diaryId).orElseThrow(() -> new RuntimeException("Diary entry not found."));
        Long mySection = diaryRepo.findSectionIdByStudent(studentId);
        if (mySection == null || !mySection.equals(d.getSectionId())) {
            throw new RuntimeException("This homework was not given to your class.");
        }
        DiaryResponse r = responseRepo.findByDiaryIdAndStudentId(diaryId, studentId)
                .orElseGet(() -> {
                    DiaryResponse x = new DiaryResponse();
                    x.setDiaryId(diaryId);
                    x.setStudentId(studentId);
                    return x;
                });
        if (status != null && !status.isBlank()) r.setStatus(status.trim().toUpperCase());
        if (note != null) r.setNote(note.isBlank() ? null : note.trim());
        DiaryResponse saved = responseRepo.save(r);
        Map<String, Object> out = new HashMap<>();
        out.put("status", saved.getStatus());
        out.put("note", saved.getNote());
        return out;
    }

    /** Teacher/admin report: for a section (optionally a date), every entry with the full roster's responses. */
    public List<Map<String, Object>> sectionResponses(Long sectionId, LocalDate date) {
        List<DiaryDto> entries = listForSection(sectionId);
        if (date != null) {
            entries = entries.stream()
                    .filter(e -> e.getCreatedAt() != null && e.getCreatedAt().toLocalDate().equals(date))
                    .collect(Collectors.toList());
        }
        List<RosterRow> roster = responseRepo.findRoster(sectionId);
        List<Long> ids = entries.stream().map(DiaryDto::getId).collect(Collectors.toList());

        Map<Long, Map<Long, DiaryResponse>> byDiary = new HashMap<>();
        if (!ids.isEmpty()) {
            for (DiaryResponse r : responseRepo.findByDiaryIdIn(ids)) {
                byDiary.computeIfAbsent(r.getDiaryId(), k -> new HashMap<>()).put(r.getStudentId(), r);
            }
        }

        List<Map<String, Object>> out = new ArrayList<>();
        for (DiaryDto e : entries) {
            Map<Long, DiaryResponse> resp = byDiary.getOrDefault(e.getId(), Map.of());
            int done = 0, notDone = 0, pending = 0;
            List<Map<String, Object>> students = new ArrayList<>();
            for (RosterRow row : roster) {
                DiaryResponse dr = resp.get(row.getStudentId());
                String st = dr == null ? "PENDING" : dr.getStatus();
                if ("DONE".equals(st)) done++;
                else if ("NOT_DONE".equals(st)) notDone++;
                else pending++;
                Map<String, Object> sm = new HashMap<>();
                sm.put("studentId", row.getStudentId());
                sm.put("studentName", row.getStudentName());
                sm.put("rollNo", row.getRollNo());
                sm.put("status", st);
                sm.put("note", dr == null ? null : dr.getNote());
                sm.put("respondedAt", dr == null ? null : dr.getUpdatedAt());
                students.add(sm);
            }
            Map<String, Object> em = new HashMap<>();
            em.put("diaryId", e.getId());
            em.put("title", e.getTitle());
            em.put("subjectName", e.getSubjectName());
            em.put("teacherName", e.getTeacherName());
            em.put("postedAt", e.getCreatedAt());
            em.put("dueDate", e.getDueDate());
            em.put("done", done);
            em.put("notDone", notDone);
            em.put("pending", pending);
            em.put("total", roster.size());
            em.put("students", students);
            out.add(em);
        }
        return out;
    }

    /** Sections the current teacher is class-teacher of (for the posting UI). */
    public List<Map<String, Object>> mySections(User user) {
        Long tid = teacherIdOf(user);
        if (tid == null) return List.of();
        return diaryRepo.findSectionsForClassTeacher(tid).stream()
                .map(s -> Map.<String, Object>of("sectionId", s.getSectionId(), "label", s.getLabel()))
                .collect(Collectors.toList());
    }

    /** (section + subject) allocations this teacher teaches — for posting work per subject. */
    public List<Map<String, Object>> myAllocations(User user) {
        Long tid = teacherIdOf(user);
        if (tid == null) return List.of();
        return diaryRepo.findAllocationsForTeacher(tid).stream()
                .map(a -> Map.<String, Object>of(
                        "sectionId", a.getSectionId(),
                        "sectionLabel", a.getSectionLabel(),
                        "topicId", a.getTopicId(),
                        "subjectName", a.getSubjectName()))
                .collect(Collectors.toList());
    }

    /** All sections + current class teacher (admin assignment screen). */
    public List<Map<String, Object>> adminSections() {
        return diaryRepo.findAllSectionsWithTeacher().stream()
                .map(s -> {
                    java.util.HashMap<String, Object> m = new java.util.HashMap<>();
                    m.put("sectionId", s.getSectionId());
                    m.put("label", s.getLabel());
                    m.put("classTeacherId", s.getClassTeacherId());
                    m.put("teacherName", s.getTeacherName());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignClassTeacher(Long sectionId, Long teacherId) {
        diaryRepo.assignClassTeacher(sectionId, teacherId);
    }

    @Transactional
    public DiaryDto create(DiaryCreateRequest req, User user) {
        Long tid = teacherIdOf(user);
        Long classTeacherId = diaryRepo.findClassTeacherId(req.getSectionId());
        boolean teachesSection = tid != null && diaryRepo.countTeacherInSection(tid, req.getSectionId()) > 0;
        boolean allowed = isAdmin(user) || (tid != null && tid.equals(classTeacherId)) || teachesSection;
        if (!allowed) {
            throw new RuntimeException("You can only post the diary for a section you teach.");
        }
        Diary d = new Diary();
        d.setTitle(req.getTitle());
        d.setDescription(req.getDescription());
        d.setDueDate(req.getDueDate());
        d.setSubjectId(req.getSubjectId());
        d.setSectionId(req.getSectionId());
        d.setTeacherId(tid != null ? tid : classTeacherId);
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        Diary saved = diaryRepo.save(d);
        // return enriched view of the section's newest entry
        return listForSection(req.getSectionId()).stream()
                .filter(x -> x.getId().equals(saved.getId())).findFirst()
                .orElse(DiaryDto.builder().id(saved.getId()).title(saved.getTitle()).build());
    }

    @Transactional
    public void delete(Long id) {
        diaryRepo.deleteById(id);
    }
}
