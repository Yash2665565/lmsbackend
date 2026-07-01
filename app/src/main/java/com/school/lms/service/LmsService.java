package com.school.lms.service;

import com.school.academics.repository.SubjectRepository;
import com.school.common.exception.NotFoundException;
import com.school.identity.entity.User;
import com.school.identity.repository.StudentRepository;
import com.school.identity.repository.TeacherRepository;
import com.school.lms.dto.*;
import com.school.lms.entity.*;
import com.school.lms.repository.*;
import com.school.lms.repository.LmsCourseRepository.CourseRow;
import com.school.lms.repository.LmsCourseRepository.MySectionRow;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LmsService {

    private final UnitRepository unitRepo;
    private final CourseNoteRepository noteRepo;
    private final AssignmentRepository assignmentRepo;
    private final AssignmentSubmissionRepository submissionRepo;
    private final SubjectRepository subjectRepo;
    private final SurpriseTestRepository surpriseTestRepo;
    private final LmsCourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;

    // ── Scoped course (subject) listing ──────────────────────────────────────

    private boolean hasAuthority(User user, String fragment) {
        for (GrantedAuthority a : user.getAuthorities()) {
            if (a.getAuthority().toUpperCase().contains(fragment)) return true;
        }
        return false;
    }

    private Map<String, Object> courseToMap(CourseRow r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("name", r.getName());
        m.put("code", r.getCode());
        m.put("description", r.getDescription());
        return m;
    }

    /**
     * Courses (subjects) scoped to the caller:
     *  - STUDENT: subjects of their own class (sectionId ignored)
     *  - TEACHER: subjects they teach in the chosen section; all their subjects if no section
     *  - ADMIN:   subjects of the chosen section's class; all subjects if no section
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listCourses(User user, Long sectionId) {
        if (hasAuthority(user, "ADMIN")) {
            if (sectionId != null) {
                return courseRepo.classSubjectsOfSection(sectionId).stream().map(this::courseToMap).collect(Collectors.toList());
            }
            // no section → every subject (admins browsing the catalogue)
            return subjectRepo.findAll().stream().map(s -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", s.getId());
                m.put("name", s.getName());
                m.put("code", s.getCode());
                m.put("description", s.getDescription());
                return m;
            }).collect(Collectors.toList());
        }
        if (hasAuthority(user, "TEACHER")) {
            Long teacherId = teacherRepo.findByUserId(user.getId()).map(t -> t.getId()).orElse(null);
            if (teacherId == null) return new ArrayList<>();
            List<CourseRow> rows = (sectionId != null)
                    ? courseRepo.teacherSubjectsInSection(teacherId, sectionId)
                    : courseRepo.teacherAllSubjects(teacherId);
            return rows.stream().map(this::courseToMap).collect(Collectors.toList());
        }
        if (hasAuthority(user, "STUDENT")) {
            Long studentId = studentRepo.findByUserId(user.getId()).map(s -> s.getId()).orElse(null);
            if (studentId == null) return new ArrayList<>();
            return courseRepo.studentSubjects(studentId).stream().map(this::courseToMap).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /** Classes & sections the current teacher is allocated to — used to scope the LMS filter. */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> myTeachingSections(User user) {
        Long teacherId = teacherRepo.findByUserId(user.getId()).map(t -> t.getId()).orElse(null);
        if (teacherId == null) return new ArrayList<>();
        return courseRepo.teacherSections(teacherId).stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("sectionId", r.getSectionId());
            m.put("sectionName", r.getSectionName());
            m.put("classId", r.getClassId());
            m.put("className", r.getClassName());
            return m;
        }).collect(Collectors.toList());
    }

    // ── Units ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UnitDto> listUnits(Long subjectId) {
        List<Unit> units = unitRepo.findBySubjectIdOrderByOrderNoAsc(subjectId);
        List<Long> unitIds = units.stream().map(Unit::getId).toList();
        Map<Long, Long> notesCounts  = noteRepo.findAll().stream()
                .filter(n -> unitIds.contains(n.getUnitId()))
                .collect(Collectors.groupingBy(CourseNote::getUnitId, Collectors.counting()));
        Map<Long, Long> assignCounts = assignmentRepo.findByUnitIdIn(unitIds).stream()
                .collect(Collectors.groupingBy(Assignment::getUnitId, Collectors.counting()));
        return units.stream().map(u -> UnitDto.builder()
                .id(u.getId())
                .subjectId(u.getSubjectId())
                .title(u.getTitle())
                .description(u.getDescription())
                .orderNo(u.getOrderNo())
                .notesCount(notesCounts.getOrDefault(u.getId(), 0L).intValue())
                .assignmentsCount(assignCounts.getOrDefault(u.getId(), 0L).intValue())
                .createdAt(u.getCreatedAt())
                .build())
                .collect(Collectors.toList());
    }

    public UnitDto createUnit(Long subjectId, String title, String description, int orderNo) {
        if (!subjectRepo.existsById(subjectId))
            throw new NotFoundException("Subject not found: " + subjectId);
        Unit unit = new Unit();
        unit.setSubjectId(subjectId);
        unit.setTitle(title);
        unit.setDescription(description);
        unit.setOrderNo(orderNo);
        unit.setCreatedAt(LocalDateTime.now());
        unit.setUpdatedAt(LocalDateTime.now());
        return toUnitDto(unitRepo.save(unit));
    }

    public UnitDto updateUnit(Long unitId, String title, String description, int orderNo) {
        Unit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NotFoundException("Unit not found: " + unitId));
        unit.setTitle(title);
        unit.setDescription(description);
        unit.setOrderNo(orderNo);
        unit.setUpdatedAt(LocalDateTime.now());
        return toUnitDto(unitRepo.save(unit));
    }

    public void deleteUnit(Long unitId) {
        unitRepo.deleteById(unitId);
    }

    // ── Notes ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CourseNoteDto> listNotes(Long unitId) {
        return noteRepo.findByUnitIdOrderByCreatedAtAsc(unitId).stream()
                .map(this::toNoteDto).toList();
    }

    public CourseNoteDto createNote(Long unitId, String title, String content,
                                    String fileUrl, CourseNote.NoteType noteType) {
        unitRepo.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found: " + unitId));
        CourseNote note = new CourseNote();
        note.setUnitId(unitId);
        note.setTitle(title);
        note.setContent(content);
        note.setFileUrl(fileUrl);
        note.setNoteType(noteType != null ? noteType : CourseNote.NoteType.TEXT);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        return toNoteDto(noteRepo.save(note));
    }

    public void deleteNote(Long noteId) {
        noteRepo.deleteById(noteId);
    }

    // ── Assignments ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AssignmentDto> listAssignments(Long unitId, Long studentId) {
        return assignmentRepo.findByUnitIdOrderByDueDateAsc(unitId).stream().map(a -> {
            AssignmentDto.AssignmentDtoBuilder b = AssignmentDto.builder()
                    .id(a.getId()).unitId(a.getUnitId()).title(a.getTitle())
                    .description(a.getDescription()).dueDate(a.getDueDate())
                    .maxMarks(a.getMaxMarks()).createdAt(a.getCreatedAt());
            if (studentId != null) {
                submissionRepo.findByAssignmentIdAndStudentId(a.getId(), studentId)
                        .ifPresent(s -> b.mySubmission(toSubmissionDto(s)));
            }
            return b.build();
        }).toList();
    }

    public AssignmentDto createAssignment(Long unitId, String title, String description,
                                          java.time.LocalDate dueDate, int maxMarks, Long createdBy) {
        unitRepo.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found: " + unitId));
        Assignment a = new Assignment();
        a.setUnitId(unitId);
        a.setTitle(title);
        a.setDescription(description);
        a.setDueDate(dueDate);
        a.setMaxMarks(maxMarks);
        a.setCreatedBy(createdBy);
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());
        return toAssignmentDto(assignmentRepo.save(a), null);
    }

    public void deleteAssignment(Long assignmentId) {
        assignmentRepo.deleteById(assignmentId);
    }

    // ── Submissions ────────────────────────────────────────────────────────

    public AssignmentSubmissionDto submit(Long assignmentId, Long studentId, String content) {
        assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + assignmentId));
        AssignmentSubmission sub = submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElse(new AssignmentSubmission());
        sub.setAssignmentId(assignmentId);
        sub.setStudentId(studentId);
        sub.setContent(content);
        sub.setSubmittedAt(LocalDateTime.now());
        sub.setStatus(AssignmentSubmission.SubmissionStatus.SUBMITTED);
        return toSubmissionDto(submissionRepo.save(sub));
    }

    @Transactional(readOnly = true)
    public AssignmentSubmissionDto getMySubmission(Long assignmentId, Long studentId) {
        return submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .map(this::toSubmissionDto).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AssignmentSubmissionDto> listSubmissions(Long assignmentId) {
        return submissionRepo.findByAssignmentId(assignmentId).stream()
                .map(this::toSubmissionDto).toList();
    }

    public AssignmentSubmissionDto gradeSubmission(Long submissionId,
                                                    java.math.BigDecimal marks, String feedback) {
        AssignmentSubmission sub = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found: " + submissionId));
        sub.setMarksObtained(marks);
        sub.setFeedback(feedback);
        sub.setStatus(AssignmentSubmission.SubmissionStatus.GRADED);
        return toSubmissionDto(submissionRepo.save(sub));
    }

    // ── Stats ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public LmsStatsDto getStudentStats(Long studentId) {
        int activeCourses = (int) subjectRepo.count();
        long submitted    = submissionRepo.countByStudentId(studentId);

        List<Long> allAssignmentIds = assignmentRepo.findAll().stream()
                .map(Assignment::getId).toList();
        List<Long> submittedIds = submissionRepo.findByStudentId(studentId).stream()
                .map(AssignmentSubmission::getAssignmentId).toList();
        int pending = (int) allAssignmentIds.stream().filter(id -> !submittedIds.contains(id)).count();

        return LmsStatsDto.builder()
                .activeCourses(activeCourses)
                .submissionsCount(submitted)
                .pendingAssignments(pending)
                .build();
    }

    // ── Surprise Tests ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SurpriseTestDto> listSurpriseTests(Long unitId) {
        return surpriseTestRepo.findByUnitIdOrderByTestDateAsc(unitId).stream()
                .map(this::toSurpriseTestDto).toList();
    }

    public SurpriseTestDto createSurpriseTest(Long unitId, String title, String description,
                                               java.time.LocalDate testDate, int durationMinutes,
                                               int maxMarks, Long createdBy) {
        unitRepo.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found: " + unitId));
        SurpriseTest st = new SurpriseTest();
        st.setUnitId(unitId);
        st.setTitle(title);
        st.setDescription(description);
        st.setTestDate(testDate);
        st.setDurationMinutes(durationMinutes > 0 ? durationMinutes : 30);
        st.setMaxMarks(maxMarks > 0 ? maxMarks : 20);
        st.setCreatedBy(createdBy);
        st.setCreatedAt(LocalDateTime.now());
        st.setUpdatedAt(LocalDateTime.now());
        return toSurpriseTestDto(surpriseTestRepo.save(st));
    }

    public SurpriseTestDto updateSurpriseTest(Long testId, String title, String description,
                                               java.time.LocalDate testDate, int durationMinutes, int maxMarks) {
        SurpriseTest st = surpriseTestRepo.findById(testId)
                .orElseThrow(() -> new NotFoundException("Surprise test not found: " + testId));
        st.setTitle(title);
        st.setDescription(description);
        st.setTestDate(testDate);
        st.setDurationMinutes(durationMinutes > 0 ? durationMinutes : st.getDurationMinutes());
        st.setMaxMarks(maxMarks > 0 ? maxMarks : st.getMaxMarks());
        st.setUpdatedAt(LocalDateTime.now());
        return toSurpriseTestDto(surpriseTestRepo.save(st));
    }

    public void deleteSurpriseTest(Long testId) {
        surpriseTestRepo.deleteById(testId);
    }

    // ── Mappers ────────────────────────────────────────────────────────────

    private UnitDto toUnitDto(Unit u) {
        return UnitDto.builder().id(u.getId()).subjectId(u.getSubjectId())
                .title(u.getTitle()).description(u.getDescription())
                .orderNo(u.getOrderNo()).createdAt(u.getCreatedAt()).build();
    }

    private CourseNoteDto toNoteDto(CourseNote n) {
        return CourseNoteDto.builder().id(n.getId()).unitId(n.getUnitId())
                .title(n.getTitle()).content(n.getContent()).fileUrl(n.getFileUrl())
                .noteType(n.getNoteType()).createdAt(n.getCreatedAt()).build();
    }

    private AssignmentDto toAssignmentDto(Assignment a, AssignmentSubmissionDto sub) {
        return AssignmentDto.builder().id(a.getId()).unitId(a.getUnitId())
                .title(a.getTitle()).description(a.getDescription())
                .dueDate(a.getDueDate()).maxMarks(a.getMaxMarks())
                .createdAt(a.getCreatedAt()).mySubmission(sub).build();
    }

    private SurpriseTestDto toSurpriseTestDto(SurpriseTest st) {
        return SurpriseTestDto.builder()
                .id(st.getId()).unitId(st.getUnitId()).title(st.getTitle())
                .description(st.getDescription()).testDate(st.getTestDate())
                .durationMinutes(st.getDurationMinutes()).maxMarks(st.getMaxMarks())
                .createdAt(st.getCreatedAt()).build();
    }

    private AssignmentSubmissionDto toSubmissionDto(AssignmentSubmission s) {
        return AssignmentSubmissionDto.builder().id(s.getId())
                .assignmentId(s.getAssignmentId()).studentId(s.getStudentId())
                .content(s.getContent()).submittedAt(s.getSubmittedAt())
                .marksObtained(s.getMarksObtained()).feedback(s.getFeedback())
                .status(s.getStatus()).build();
    }
}
