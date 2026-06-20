package com.school.attendance.service;

import com.school.academics.entity.Section;
import com.school.academics.entity.Term;
import com.school.academics.repository.SectionRepository;
import com.school.academics.repository.TermRepository;
import com.school.attendance.dto.AttendanceMarkDto;
import com.school.attendance.dto.AttendanceRecordDto;
import com.school.attendance.dto.AttendanceSubmitRequest;
import com.school.attendance.dto.AttendanceSummaryDto;
import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.AttendanceSummary;
import com.school.attendance.repository.AttendanceRecordRepository;
import com.school.attendance.repository.AttendanceSummaryRepository;
import com.school.common.exception.BadRequestException;
import com.school.common.exception.NotFoundException;
import com.school.identity.entity.Student;
import com.school.identity.entity.User;
import com.school.identity.repository.StudentRepository;
import com.school.identity.repository.UserRepository;
import com.school.timetable.entity.Period;
import com.school.timetable.repository.PeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final TermRepository termRepository;
    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final PeriodRepository periodRepository;
    private final UserRepository userRepository;

    // -------------------------------------------------------------------------
    // Submit / update attendance for a section
    // -------------------------------------------------------------------------

    public void submitAttendance(Long sectionId, AttendanceSubmitRequest req, User teacher) {
        Term term = termRepository.findTermForDate(req.getDate())
                .orElseThrow(() -> new BadRequestException("No term configured for this date"));

        Period period = req.getPeriodId() != null
                ? periodRepository.findById(req.getPeriodId()).orElse(null)
                : null;

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found"));

        for (AttendanceMarkDto mark : req.getMarks()) {
            Student student = studentRepository.findById(mark.getStudentId())
                    .orElseThrow(() -> new NotFoundException("Student not found: " + mark.getStudentId()));

            AttendanceRecord.AttendanceStatus statusEnum =
                    AttendanceRecord.AttendanceStatus.valueOf(mark.getStatus());

            Optional<AttendanceRecord> existing =
                    attendanceRecordRepository.findByStudentIdAndAttendanceDateAndPeriodId(
                            mark.getStudentId(), req.getDate(), req.getPeriodId());

            if (existing.isPresent()) {
                AttendanceRecord record = existing.get();
                record.setStatus(statusEnum);
                record.setRemarks(mark.getRemarks());
                record.setMarkedBy(teacher);
                record.setUpdatedAt(LocalDateTime.now());
                attendanceRecordRepository.save(record);
            } else {
                AttendanceRecord record = new AttendanceRecord();
                record.setStudent(student);
                record.setSection(section);
                record.setAttendanceDate(req.getDate());
                record.setPeriod(period);
                record.setStatus(statusEnum);
                record.setMarkedBy(teacher);
                record.setRemarks(mark.getRemarks());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                attendanceRecordRepository.save(record);
            }

            recomputeSummary(mark.getStudentId(), term.getId());
        }
    }

    // -------------------------------------------------------------------------
    // Recompute attendance summary for a student within a term (daily only)
    // -------------------------------------------------------------------------

    private void recomputeSummary(Long studentId, Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new NotFoundException("Term not found: " + termId));

        List<AttendanceRecord> records =
                attendanceRecordRepository.findByStudentIdAndAttendanceDateBetween(
                        studentId, term.getStartDate(), term.getEndDate())
                        .stream()
                        .filter(r -> r.getPeriod() == null)
                        .toList();

        int presentCount = (int) records.stream()
                .filter(r -> r.getStatus() == AttendanceRecord.AttendanceStatus.PRESENT)
                .count();

        int lateCount = (int) records.stream()
                .filter(r -> r.getStatus() == AttendanceRecord.AttendanceStatus.LATE
                        || r.getStatus() == AttendanceRecord.AttendanceStatus.HALF_DAY)
                .count();

        int absentCount = (int) records.stream()
                .filter(r -> r.getStatus() == AttendanceRecord.AttendanceStatus.ABSENT
                        || r.getStatus() == AttendanceRecord.AttendanceStatus.EXCUSED)
                .count();

        int total = presentCount + lateCount + absentCount;

        BigDecimal pct = total == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf((presentCount + lateCount) * 100.0 / total)
                        .setScale(2, RoundingMode.HALF_UP);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

        AttendanceSummary summary = attendanceSummaryRepository
                .findByStudentIdAndTermId(studentId, termId)
                .orElse(new AttendanceSummary());

        summary.setStudent(student);
        summary.setTerm(term);
        summary.setPresentDays(presentCount);
        summary.setLateDays(lateCount);
        summary.setAbsentDays(absentCount);
        summary.setTotalDays(total);
        summary.setAttendancePct(pct);
        summary.setUpdatedAt(LocalDateTime.now());

        attendanceSummaryRepository.save(summary);
    }

    // -------------------------------------------------------------------------
    // Query methods
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AttendanceRecordDto> getDailyRegister(Long sectionId, java.time.LocalDate date) {
        return attendanceRecordRepository.findDailyRegister(sectionId, date)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceSummaryDto> getStudentSummary(Long studentId) {
        return attendanceSummaryRepository.findByStudentId(studentId)
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceSummaryDto> getLowAttendance(Long termId, BigDecimal threshold) {
        return attendanceSummaryRepository.findLowAttendance(termId, threshold)
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceSummaryDto> getDailyAbsentees(java.time.LocalDate date) {
        List<Long> absentIds = attendanceRecordRepository.findAbsentStudentIds(date);
        return absentIds.stream()
                .map(id -> {
                    Student student = studentRepository.findById(id).orElse(null);
                    return toSimpleSummaryDto(student);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Private mapping helpers
    // -------------------------------------------------------------------------

    private AttendanceRecordDto toDto(AttendanceRecord record) {
        AttendanceRecordDto dto = new AttendanceRecordDto();
        dto.setId(record.getId());
        dto.setStudentId(record.getStudent().getId());
        dto.setStudentName(record.getStudent().getFullName());
        dto.setAdmissionNo(record.getStudent().getAdmissionNo());
        dto.setStatus(record.getStatus().name());
        dto.setDate(record.getAttendanceDate());
        dto.setPeriodId(record.getPeriod() != null ? record.getPeriod().getId() : null);
        dto.setRemarks(record.getRemarks());
        return dto;
    }

    private AttendanceSummaryDto toSummaryDto(AttendanceSummary summary) {
        AttendanceSummaryDto dto = new AttendanceSummaryDto();
        dto.setStudentId(summary.getStudent().getId());
        dto.setStudentName(summary.getStudent().getFullName());
        dto.setAdmissionNo(summary.getStudent().getAdmissionNo());
        dto.setTermId(summary.getTerm().getId());
        dto.setTermName(summary.getTerm().getName());
        dto.setPresentDays(summary.getPresentDays());
        dto.setAbsentDays(summary.getAbsentDays());
        dto.setLateDays(summary.getLateDays());
        dto.setTotalDays(summary.getTotalDays());
        dto.setAttendancePct(summary.getAttendancePct());
        return dto;
    }

    private AttendanceSummaryDto toSimpleSummaryDto(Student student) {
        if (student == null) return null;
        AttendanceSummaryDto dto = new AttendanceSummaryDto();
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFullName());
        dto.setAdmissionNo(student.getAdmissionNo());
        return dto;
    }
}
