package com.school.academics.service;

import com.school.academics.entity.AcademicYear;
import com.school.academics.entity.Section;
import com.school.academics.entity.Subject;
import com.school.academics.entity.SubjectTeacher;
import com.school.academics.repository.AcademicYearRepository;
import com.school.academics.repository.SubjectTeacherRepository;
import com.school.academics.repository.SubjectTeacherRepository.SubjectRow;
import com.school.academics.repository.SubjectTeacherRepository.TeachingRow;
import com.school.identity.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final SubjectTeacherRepository repo;
    private final AcademicYearRepository academicYearRepository;
    private final com.school.academics.repository.TeacherExpertiseRepository expertiseRepo;
    private final com.school.academics.repository.ClassSubjectRepository classSubjectRepo;

    /* ── Class ↔ subject mapping (which subjects a class teaches) ── */
    @Transactional
    public void addClassSubject(Long classGradeId, Long topicId) {
        if (classSubjectRepo.findByClassGradeIdAndTopicId(classGradeId, topicId).isPresent()) return;
        com.school.academics.entity.ClassSubject cs = new com.school.academics.entity.ClassSubject();
        cs.setClassGradeId(classGradeId);
        cs.setTopicId(topicId);
        classSubjectRepo.save(cs);
    }

    @Transactional
    public void removeClassSubject(Long classGradeId, Long topicId) {
        classSubjectRepo.findByClassGradeIdAndTopicId(classGradeId, topicId)
                .ifPresent(classSubjectRepo::delete);
    }

    private Long currentYearId() {
        return academicYearRepository.findByIsCurrent(true)
                .map(AcademicYear::getId)
                .orElseGet(() -> academicYearRepository.findAll().stream().findFirst().map(AcademicYear::getId).orElse(1L));
    }

    /** Subjects of a section + who's assigned to each. */
    public List<SubjectRow> sectionSubjects(Long sectionId) {
        return repo.findSectionSubjects(sectionId);
    }

    /* ── Teacher subject expertise ── */
    public List<com.school.academics.repository.TeacherExpertiseRepository.SubjectLite> getExpertise(Long teacherId) {
        return expertiseRepo.findSubjectsForTeacher(teacherId);
    }

    public List<com.school.academics.repository.TeacherExpertiseRepository.TeacherLite> teachersForSubject(Long topicId) {
        return expertiseRepo.findTeachersForSubject(topicId);
    }

    @Transactional
    public void setExpertise(Long teacherId, List<Long> subjectIds) {
        expertiseRepo.deleteByTeacherId(teacherId);
        if (subjectIds == null) return;
        for (Long topicId : subjectIds.stream().distinct().collect(Collectors.toList())) {
            com.school.academics.entity.TeacherExpertise te = new com.school.academics.entity.TeacherExpertise();
            te.setTeacherId(teacherId);
            te.setTopicId(topicId);
            expertiseRepo.save(te);
        }
    }

    @Transactional
    public void assign(Long sectionId, Long topicId, Long teacherId) {
        SubjectTeacher st = repo.findBySection_IdAndSubject_Id(sectionId, topicId).orElseGet(SubjectTeacher::new);

        Teacher te = new Teacher(); te.setId(teacherId); st.setTeacher(te);
        Subject sub = new Subject(); sub.setId(topicId); st.setSubject(sub);
        Section sec = new Section(); sec.setId(sectionId); st.setSection(sec);
        AcademicYear ay = new AcademicYear(); ay.setId(currentYearId()); st.setAcademicYear(ay);

        LocalDateTime now = LocalDateTime.now();
        if (st.getId() == null) st.setCreatedAt(now);
        st.setUpdatedAt(now);
        repo.save(st);
    }

    @Transactional
    public void unassign(Long assignmentId) {
        repo.deleteById(assignmentId);
    }

    /** Full report for one teacher: sections + subjects taught, class-teacher-of, counts. */
    public Map<String, Object> teacherReport(Long teacherId) {
        List<TeachingRow> teaching = repo.findTeaching(teacherId);
        List<String> classTeacherOf = repo.findClassTeacherOf(teacherId);
        long sectionCount = teaching.stream().map(TeachingRow::getSectionId).distinct().count();

        Map<String, Object> out = new HashMap<>();
        out.put("teacherId", teacherId);
        out.put("teaching", teaching.stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("assignmentId", t.getAssignmentId());
            m.put("sectionId", t.getSectionId());
            m.put("sectionLabel", t.getSectionLabel());
            m.put("subjectName", t.getSubjectName());
            return m;
        }).collect(Collectors.toList()));
        out.put("classTeacherOf", classTeacherOf);
        out.put("sectionCount", sectionCount);
        out.put("subjectCount", teaching.size());
        return out;
    }
}
