package com.school.academics.service;

import com.school.academics.dto.*;
import com.school.academics.entity.*;
import com.school.academics.repository.*;
import com.school.common.exception.BadRequestException;
import com.school.common.exception.NotFoundException;
import com.school.identity.entity.Student;
import com.school.identity.entity.User;
import com.school.identity.repository.StudentRepository;
import com.school.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicsService {

    private final AcademicYearRepository academicYearRepository;
    private final TermRepository termRepository;
    private final ClassGradeRepository classGradeRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    // ─── AcademicYear ───────────────────────────────────────────────────────

    public List<AcademicYearDto> listYears() {
        return academicYearRepository.findAll()
                .stream()
                .map(this::toYearDto)
                .toList();
    }

    public AcademicYearDto createYear(AcademicYearDto req) {
        if (req.isCurrent()) {
            academicYearRepository.findAll().forEach(y -> {
                if (y.isCurrent()) {
                    y.setCurrent(false);
                    y.setUpdatedAt(LocalDateTime.now());
                    academicYearRepository.save(y);
                }
            });
        }
        AcademicYear year = new AcademicYear();
        year.setName(req.getName());
        year.setStartDate(req.getStartDate());
        year.setEndDate(req.getEndDate());
        year.setCurrent(req.isCurrent());
        year.setCreatedAt(LocalDateTime.now());
        year.setUpdatedAt(LocalDateTime.now());
        return toYearDto(academicYearRepository.save(year));
    }

    public AcademicYear getCurrentYear() {
        return academicYearRepository.findByIsCurrent(true)
                .orElseThrow(() -> new NotFoundException("No current academic year found"));
    }

    // ─── Term ────────────────────────────────────────────────────────────────

    public List<TermDto> listTerms(Long yearId) {
        return termRepository.findByAcademicYearId(yearId)
                .stream()
                .map(this::toTermDto)
                .toList();
    }

    public TermDto createTerm(TermDto req) {
        AcademicYear year = academicYearRepository.findById(req.getAcademicYearId())
                .orElseThrow(() -> new NotFoundException("Academic year not found: " + req.getAcademicYearId()));
        Term term = new Term();
        term.setAcademicYear(year);
        term.setName(req.getName());
        term.setStartDate(req.getStartDate());
        term.setEndDate(req.getEndDate());
        term.setCreatedAt(LocalDateTime.now());
        term.setUpdatedAt(LocalDateTime.now());
        return toTermDto(termRepository.save(term));
    }

    // ─── ClassGrade ──────────────────────────────────────────────────────────

    public List<ClassGradeDto> listClasses() {
        return classGradeRepository.findAll()
                .stream()
                .map(this::toClassGradeDto)
                .toList();
    }

    public ClassGradeDto createClass(ClassGradeDto req) {
        ClassGrade grade = new ClassGrade();
        grade.setName(req.getName());
        grade.setCreatedAt(LocalDateTime.now());
        grade.setUpdatedAt(LocalDateTime.now());
        return toClassGradeDto(classGradeRepository.save(grade));
    }

    // ─── Section ─────────────────────────────────────────────────────────────

    public List<SectionDto> listSections(Long classGradeId) {
        return sectionRepository.findByClassGradeId(classGradeId)
                .stream()
                .map(this::toSectionDto)
                .toList();
    }

    public SectionDto createSection(SectionDto req) {
        ClassGrade grade = classGradeRepository.findById(req.getClassGradeId())
                .orElseThrow(() -> new NotFoundException("Class grade not found: " + req.getClassGradeId()));
        Section section = new Section();
        section.setClassGrade(grade);
        section.setName(req.getName());
        if (req.getClassTeacherId() != null) {
            User teacher = userRepository.findById(req.getClassTeacherId())
                    .orElseThrow(() -> new NotFoundException("User not found: " + req.getClassTeacherId()));
            section.setClassTeacher(teacher);
        }
        section.setCreatedAt(LocalDateTime.now());
        section.setUpdatedAt(LocalDateTime.now());
        return toSectionDto(sectionRepository.save(section));
    }

    public SectionDto updateSection(Long id, SectionDto req) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section not found: " + id));
        if (req.getName() != null) {
            section.setName(req.getName());
        }
        if (req.getClassTeacherId() != null) {
            User teacher = userRepository.findById(req.getClassTeacherId())
                    .orElseThrow(() -> new NotFoundException("User not found: " + req.getClassTeacherId()));
            section.setClassTeacher(teacher);
        } else {
            section.setClassTeacher(null);
        }
        section.setUpdatedAt(LocalDateTime.now());
        return toSectionDto(sectionRepository.save(section));
    }

    // ─── Subject ─────────────────────────────────────────────────────────────

    public List<SubjectDto> listSubjects() {
        return subjectRepository.findByIsHiddenFalseOrderByPosition()
                .stream()
                .map(this::toSubjectDto)
                .toList();
    }

    public SubjectDto createSubject(SubjectDto req) {
        Subject subject = new Subject();
        subject.setName(req.getName());
        subject.setCode(req.getCode());
        subject.setDescription(req.getDescription());
        subject.setHidden(false);
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());
        return toSubjectDto(subjectRepository.save(subject));
    }

    public SubjectDto updateSubject(Long id, SubjectDto req) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject not found: " + id));
        if (req.getName() != null) subject.setName(req.getName());
        if (req.getCode() != null) subject.setCode(req.getCode());
        if (req.getDescription() != null) subject.setDescription(req.getDescription());
        subject.setUpdatedAt(LocalDateTime.now());
        return toSubjectDto(subjectRepository.save(subject));
    }

    // ─── Enrollment ──────────────────────────────────────────────────────────

    public List<EnrollmentDto> listRoster(Long sectionId, Long academicYearId) {
        return enrollmentRepository.findBySectionIdAndAcademicYearId(sectionId, academicYearId)
                .stream()
                .map(this::toEnrollmentDto)
                .toList();
    }

    public EnrollmentDto enroll(EnrollmentRequest req) {
        enrollmentRepository.findByStudentIdAndAcademicYearId(req.getStudentId(), req.getAcademicYearId())
                .ifPresent(e -> {
                    throw new BadRequestException(
                            "Student is already enrolled in this academic year (enrollment id: " + e.getId() + ")");
                });
        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found: " + req.getStudentId()));
        Section section = sectionRepository.findById(req.getSectionId())
                .orElseThrow(() -> new NotFoundException("Section not found: " + req.getSectionId()));
        AcademicYear year = academicYearRepository.findById(req.getAcademicYearId())
                .orElseThrow(() -> new NotFoundException("Academic year not found: " + req.getAcademicYearId()));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setAcademicYear(year);
        enrollment.setRollNo(req.getRollNo());
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollment.setCreatedAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());
        return toEnrollmentDto(enrollmentRepository.save(enrollment));
    }

    public void updateEnrollmentStatus(Long id, String status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + id));
        enrollment.setStatus(Enrollment.EnrollmentStatus.valueOf(status));
        enrollment.setUpdatedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    // ─── Private mapping helpers ──────────────────────────────────────────────

    private AcademicYearDto toYearDto(AcademicYear y) {
        return AcademicYearDto.builder()
                .id(y.getId())
                .name(y.getName())
                .startDate(y.getStartDate())
                .endDate(y.getEndDate())
                .isCurrent(y.isCurrent())
                .build();
    }

    private TermDto toTermDto(Term t) {
        return TermDto.builder()
                .id(t.getId())
                .academicYearId(t.getAcademicYear() != null ? t.getAcademicYear().getId() : null)
                .academicYearName(t.getAcademicYear() != null ? t.getAcademicYear().getName() : null)
                .name(t.getName())
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .build();
    }

    private ClassGradeDto toClassGradeDto(ClassGrade g) {
        return ClassGradeDto.builder()
                .id(g.getId())
                .name(g.getName())
                .build();
    }

    private SectionDto toSectionDto(Section s) {
        return SectionDto.builder()
                .id(s.getId())
                .classGradeId(s.getClassGrade() != null ? s.getClassGrade().getId() : null)
                .classGradeName(s.getClassGrade() != null ? s.getClassGrade().getName() : null)
                .name(s.getName())
                .classTeacherId(s.getClassTeacher() != null ? s.getClassTeacher().getId() : null)
                .classTeacherName(s.getClassTeacher() != null ? s.getClassTeacher().getFullName() : null)
                .build();
    }

    private SubjectDto toSubjectDto(Subject s) {
        return SubjectDto.builder()
                .id(s.getId())
                .name(s.getName())
                .code(s.getCode())
                .description(s.getDescription())
                .build();
    }

    private EnrollmentDto toEnrollmentDto(Enrollment e) {
        Student student = e.getStudent();
        Section section = e.getSection();
        return EnrollmentDto.builder()
                .id(e.getId())
                .studentId(student != null ? student.getId() : null)
                .studentName(student != null ? student.getFullName() : null)
                .admissionNo(student != null ? student.getAdmissionNo() : null)
                .sectionId(section != null ? section.getId() : null)
                .sectionName(section != null ? section.getName() : null)
                .className(section != null && section.getClassGrade() != null
                        ? section.getClassGrade().getName() : null)
                .academicYearId(e.getAcademicYear() != null ? e.getAcademicYear().getId() : null)
                .rollNo(e.getRollNo())
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .build();
    }
}
