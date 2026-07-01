package com.school.exams.service;

import com.school.academics.entity.Enrollment;
import com.school.academics.entity.Term;
import com.school.academics.repository.EnrollmentRepository;
import com.school.academics.repository.TermRepository;
import com.school.attendance.entity.AttendanceSummary;
import com.school.attendance.repository.AttendanceSummaryRepository;
import com.school.common.exception.NotFoundException;
import com.school.exams.dto.*;
import com.school.exams.entity.Exam;
import com.school.exams.entity.ExamSubject;
import com.school.exams.entity.GradingScale;
import com.school.exams.entity.Mark;
import com.school.exams.repository.ExamRepository;
import com.school.exams.repository.ExamSubjectRepository;
import com.school.exams.repository.GradingScaleRepository;
import com.school.exams.repository.MarkRepository;
import com.school.identity.entity.Student;
import com.school.identity.entity.User;
import com.school.identity.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamSubjectRepository examSubjectRepository;
    private final MarkRepository markRepository;
    private final GradingScaleRepository gradingScaleRepository;
    private final TermRepository termRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;

    // ---------------------------------------------------------------
    // Exams
    // ---------------------------------------------------------------

    public List<ExamDto> listByTerm(Long termId) {
        return examRepository.findByTermId(termId)
                .stream()
                .map(this::toExamDto)
                .collect(Collectors.toList());
    }

    public ExamDto createExam(ExamDto req) {
        Term term = termRepository.findById(req.getTermId())
                .orElseThrow(() -> new NotFoundException("Term not found: " + req.getTermId()));

        Exam exam = new Exam();
        exam.setName(req.getName());
        exam.setTerm(term);
        exam.setExamDate(req.getExamDate());
        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());

        return toExamDto(examRepository.save(exam));
    }

    public ExamDto getById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exam not found: " + id));
        return toExamDto(exam);
    }

    // ---------------------------------------------------------------
    // Exam Subjects
    // ---------------------------------------------------------------

    public List<ExamSubjectDto> listExamSubjects(Long examId) {
        return examSubjectRepository.findByExamId(examId)
                .stream()
                .map(this::toExamSubjectDto)
                .collect(Collectors.toList());
    }

    public ExamSubjectDto addExamSubject(ExamSubjectDto req) {
        Exam exam = examRepository.findById(req.getExamId())
                .orElseThrow(() -> new NotFoundException("Exam not found: " + req.getExamId()));

        com.school.academics.entity.Subject subject =
                new com.school.academics.entity.Subject();
        // Load the real Subject entity via a query-capable repository — wire it via id field
        // We rely on a proxy reference via EntityManager-compatible approach:
        // Since SubjectRepository is not injected here, we store just the id-based proxy.
        // Instead, we inject it via the constructor — but per the spec only specific repos are injected.
        // We use a simple workaround: set fields directly on a detached Subject with only the id set.
        subject.setId(req.getSubjectId());

        com.school.academics.entity.ClassGrade classGrade =
                new com.school.academics.entity.ClassGrade();
        classGrade.setId(req.getClassGradeId());

        ExamSubject es = new ExamSubject();
        es.setExam(exam);
        es.setSubject(subject);
        es.setClassGrade(classGrade);
        es.setMaxMarks(req.getMaxMarks());
        es.setExamDate(req.getExamDate());
        es.setStartTime(req.getStartTime());
        es.setCreatedAt(LocalDateTime.now());
        es.setUpdatedAt(LocalDateTime.now());

        return toExamSubjectDto(examSubjectRepository.save(es));
    }

    public void deleteExamSubject(Long examSubjectId) {
        examSubjectRepository.deleteById(examSubjectId);
    }

    /** Subjects mapped to a class (class_subjects) — for the datesheet dropdown. */
    public List<ExamSubjectDto> listClassSubjects(Long classGradeId) {
        return examSubjectRepository.findClassSubjects(classGradeId).stream()
                .map(s -> ExamSubjectDto.builder()
                        .subjectId(s.getId()).subjectName(s.getName())
                        .classGradeId(classGradeId).build())
                .collect(Collectors.toList());
    }

    /** A student's datesheet: papers for their class, grouped per exam. */
    public List<StudentExamDto> getStudentDatesheet(Long studentId) {
        Long classGradeId = examSubjectRepository.findClassGradeByStudent(studentId);
        if (classGradeId == null) return List.of();
        java.util.Map<Long, StudentExamDto> byExam = new java.util.LinkedHashMap<>();
        for (ExamSubject es : examSubjectRepository.findByClassGradeIdOrderByExamDateAsc(classGradeId)) {
            Long exId = es.getExam() != null ? es.getExam().getId() : null;
            if (exId == null) continue;
            StudentExamDto dto = byExam.computeIfAbsent(exId, k -> StudentExamDto.builder()
                    .examId(exId)
                    .examName(es.getExam().getName())
                    .papers(new java.util.ArrayList<>())
                    .build());
            dto.getPapers().add(StudentExamDto.Paper.builder()
                    .subjectName(es.getSubject() != null ? es.getSubject().getName() : null)
                    .examDate(es.getExamDate())
                    .startTime(es.getStartTime())
                    .maxMarks(es.getMaxMarks())
                    .build());
        }
        return new java.util.ArrayList<>(byExam.values());
    }

    // ---------------------------------------------------------------
    // Marks
    // ---------------------------------------------------------------

    public List<MarkDto> getMarks(Long examSubjectId) {
        return markRepository.findByExamSubjectId(examSubjectId)
                .stream()
                .map(this::toMarkDto)
                .collect(Collectors.toList());
    }

    public MarkDto enterMark(Long examSubjectId, MarkEntryRequest req, User enteredBy) {
        ExamSubject examSubject = examSubjectRepository.findById(examSubjectId)
                .orElseThrow(() -> new NotFoundException("ExamSubject not found: " + examSubjectId));

        BigDecimal pct = req.getMarksObtained()
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(examSubject.getMaxMarks()), 2, RoundingMode.HALF_UP);

        String grade = gradingScaleRepository.findByPct(pct)
                .map(GradingScale::getGrade)
                .orElse("N/A");

        Optional<Mark> existing = markRepository.findByStudentIdAndExamSubjectId(
                req.getStudentId(), examSubjectId);

        Mark m = existing.orElse(new Mark());

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found: " + req.getStudentId()));

        m.setStudent(student);
        m.setExamSubject(examSubject);
        m.setMarksObtained(req.getMarksObtained());
        m.setGrade(grade);
        m.setEnteredBy(enteredBy);
        m.setUpdatedAt(LocalDateTime.now());

        if (m.getId() == null) {
            m.setCreatedAt(LocalDateTime.now());
        }

        return toMarkDto(markRepository.save(m));
    }

    // ---------------------------------------------------------------
    // Report Card
    // ---------------------------------------------------------------

    public ReportCardDto getReportCard(Long studentId, Long termId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new NotFoundException("Term not found: " + termId));

        // Resolve enrollment via the term's academic year
        Long academicYearId = term.getAcademicYear().getId();
        Optional<Enrollment> enrollmentOpt =
                enrollmentRepository.findByStudentIdAndAcademicYearId(studentId, academicYearId);

        String className = null;
        String sectionName = null;

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            if (enrollment.getSection() != null) {
                sectionName = enrollment.getSection().getName();
                if (enrollment.getSection().getClassGrade() != null) {
                    className = enrollment.getSection().getClassGrade().getName();
                }
            }
        }

        List<Mark> marks = markRepository.findByStudentAndTerm(studentId, termId);

        Optional<AttendanceSummary> attendanceSummaryOpt =
                attendanceSummaryRepository.findByStudentIdAndTermId(studentId, termId);
        BigDecimal attendancePct = attendanceSummaryOpt
                .map(AttendanceSummary::getAttendancePct)
                .orElse(BigDecimal.ZERO);

        // Build subject mark DTOs
        List<ReportCardDto.SubjectMarkDto> subjectMarks = marks.stream()
                .map(mark -> {
                    ExamSubject es = mark.getExamSubject();
                    BigDecimal gradePoint = gradingScaleRepository.findByPct(
                            computePct(mark.getMarksObtained(), es.getMaxMarks()))
                            .map(GradingScale::getGradePoint)
                            .orElse(BigDecimal.ZERO);

                    return ReportCardDto.SubjectMarkDto.builder()
                            .subjectName(es.getSubject() != null ? es.getSubject().getName() : null)
                            .maxMarks(es.getMaxMarks())
                            .marksObtained(mark.getMarksObtained())
                            .grade(mark.getGrade())
                            .gradePoint(gradePoint)
                            .build();
                })
                .collect(Collectors.toList());

        // Aggregate totals
        BigDecimal totalMarksObtained = marks.stream()
                .map(Mark::getMarksObtained)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalMaxMarks = marks.stream()
                .mapToInt(m -> m.getExamSubject().getMaxMarks())
                .sum();

        BigDecimal totalPct = totalMaxMarks > 0
                ? totalMarksObtained
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalMaxMarks), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal gpa = subjectMarks.isEmpty()
                ? BigDecimal.ZERO
                : subjectMarks.stream()
                        .map(ReportCardDto.SubjectMarkDto::getGradePoint)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(subjectMarks.size()), 2, RoundingMode.HALF_UP);

        return ReportCardDto.builder()
                .studentId(studentId)
                .studentName(student.getFullName())
                .admissionNo(student.getAdmissionNo())
                .className(className)
                .sectionName(sectionName)
                .termId(termId)
                .termName(term.getName())
                .attendancePct(attendancePct)
                .subjects(subjectMarks)
                .totalPct(totalPct)
                .gpa(gpa)
                .classTeacherRemarks(null)
                .build();
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    private BigDecimal computePct(BigDecimal marksObtained, int maxMarks) {
        if (marksObtained == null || maxMarks == 0) return BigDecimal.ZERO;
        return marksObtained
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(maxMarks), 2, RoundingMode.HALF_UP);
    }

    private ExamDto toExamDto(Exam exam) {
        return ExamDto.builder()
                .id(exam.getId())
                .name(exam.getName())
                .termId(exam.getTerm() != null ? exam.getTerm().getId() : null)
                .termName(exam.getTerm() != null ? exam.getTerm().getName() : null)
                .examDate(exam.getExamDate())
                .build();
    }

    private ExamSubjectDto toExamSubjectDto(ExamSubject es) {
        return ExamSubjectDto.builder()
                .id(es.getId())
                .examId(es.getExam() != null ? es.getExam().getId() : null)
                .examName(es.getExam() != null ? es.getExam().getName() : null)
                .subjectId(es.getSubject() != null ? es.getSubject().getId() : null)
                .subjectName(es.getSubject() != null ? es.getSubject().getName() : null)
                .classGradeId(es.getClassGrade() != null ? es.getClassGrade().getId() : null)
                .className(es.getClassGrade() != null ? es.getClassGrade().getName() : null)
                .maxMarks(es.getMaxMarks())
                .examDate(es.getExamDate())
                .startTime(es.getStartTime())
                .build();
    }

    private MarkDto toMarkDto(Mark mark) {
        Student student = mark.getStudent();
        ExamSubject es = mark.getExamSubject();
        return MarkDto.builder()
                .id(mark.getId())
                .studentId(student != null ? student.getId() : null)
                .studentName(student != null ? student.getFullName() : null)
                .admissionNo(student != null ? student.getAdmissionNo() : null)
                .examSubjectId(es != null ? es.getId() : null)
                .marksObtained(mark.getMarksObtained())
                .grade(mark.getGrade())
                .maxMarks(es != null ? es.getMaxMarks() : 0)
                .build();
    }
}
