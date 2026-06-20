package com.school.timetable.service;

import com.school.academics.entity.AcademicYear;
import com.school.academics.entity.Section;
import com.school.academics.entity.Subject;
import com.school.academics.repository.AcademicYearRepository;
import com.school.academics.repository.SectionRepository;
import com.school.academics.repository.SubjectRepository;
import com.school.common.exception.NotFoundException;
import com.school.identity.entity.Teacher;
import com.school.identity.repository.StudentRepository;
import com.school.identity.repository.TeacherRepository;
import com.school.identity.repository.UserRepository;
import com.school.timetable.dto.PeriodDto;
import com.school.timetable.dto.TimetableSlotDto;
import com.school.timetable.dto.TimetableSlotRequest;
import com.school.timetable.entity.Period;
import com.school.timetable.entity.TimetableSlot;
import com.school.timetable.repository.PeriodRepository;
import com.school.timetable.repository.TimetableSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {

    private final PeriodRepository periodRepository;
    private final TimetableSlotRepository timetableSlotRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public List<PeriodDto> listPeriods() {
        return periodRepository.findAllByOrderBySortOrder()
                .stream()
                .map(this::toPeriodDto)
                .toList();
    }

    public PeriodDto createPeriod(PeriodDto req) {
        Period period = new Period();
        period.setName(req.getName());
        period.setStartTime(req.getStartTime() != null
                ? java.time.LocalTime.parse(req.getStartTime()) : null);
        period.setEndTime(req.getEndTime() != null
                ? java.time.LocalTime.parse(req.getEndTime()) : null);
        period.setSortOrder(req.getSortOrder());
        period.setCreatedAt(LocalDateTime.now());
        period.setUpdatedAt(LocalDateTime.now());
        return toPeriodDto(periodRepository.save(period));
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotDto> getSectionTimetable(Long sectionId, Long academicYearId) {
        Long effectiveYearId = academicYearId;
        if (effectiveYearId == null) {
            effectiveYearId = academicYearRepository.findByIsCurrent(true)
                    .map(AcademicYear::getId)
                    .orElse(null);
        }
        if (effectiveYearId == null) return List.of();
        return timetableSlotRepository.findBySectionIdAndAcademicYearId(sectionId, effectiveYearId)
                .stream()
                .map(this::toSlotDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotDto> getStudentTimetable(Long studentId) {
        return timetableSlotRepository.findByStudentId(studentId)
                .stream()
                .map(this::toSlotDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotDto> getMyTimetable(String email) {
        return userRepository.findByEmail(email)
                .flatMap(u -> studentRepository.findByUserId(u.getId()))
                .map(s -> getStudentTimetable(s.getId()))
                .orElse(List.of());
    }

    public TimetableSlotDto createSlot(TimetableSlotRequest req) {
        Section section = sectionRepository.findById(req.getSectionId())
                .orElseThrow(() -> new NotFoundException("Section not found: " + req.getSectionId()));
        Period period = periodRepository.findById(req.getPeriodId())
                .orElseThrow(() -> new NotFoundException("Period not found: " + req.getPeriodId()));
        Subject subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found: " + req.getSubjectId()));
        Teacher teacher = teacherRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + req.getTeacherId()));
        AcademicYear academicYear = academicYearRepository.findById(req.getAcademicYearId())
                .orElseThrow(() -> new NotFoundException("AcademicYear not found: " + req.getAcademicYearId()));

        TimetableSlot slot = new TimetableSlot();
        slot.setSection(section);
        slot.setDayOfWeek(req.getDayOfWeek());
        slot.setPeriod(period);
        slot.setSubject(subject);
        slot.setTeacher(teacher);
        slot.setAcademicYear(academicYear);
        slot.setCreatedAt(LocalDateTime.now());
        slot.setUpdatedAt(LocalDateTime.now());

        return toSlotDto(timetableSlotRepository.save(slot));
    }

    public void deleteSlot(Long id) {
        timetableSlotRepository.deleteById(id);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private PeriodDto toPeriodDto(Period p) {
        return PeriodDto.builder()
                .id(p.getId())
                .name(p.getName())
                .startTime(p.getStartTime() != null ? p.getStartTime().toString() : null)
                .endTime(p.getEndTime() != null ? p.getEndTime().toString() : null)
                .sortOrder(p.getSortOrder())
                .build();
    }

    private TimetableSlotDto toSlotDto(TimetableSlot s) {
        String teacherName = s.getTeacher() != null && s.getTeacher().getUser() != null
                ? s.getTeacher().getUser().getFullName()
                : null;

        return TimetableSlotDto.builder()
                .id(s.getId())
                .sectionId(s.getSection() != null ? s.getSection().getId() : null)
                .sectionName(s.getSection() != null ? s.getSection().getName() : null)
                .dayOfWeek(s.getDayOfWeek())
                .dayName(dayName(s.getDayOfWeek()))
                .periodId(s.getPeriod() != null ? s.getPeriod().getId() : null)
                .periodName(s.getPeriod() != null ? s.getPeriod().getName() : null)
                .startTime(s.getPeriod() != null && s.getPeriod().getStartTime() != null
                        ? s.getPeriod().getStartTime().toString() : null)
                .endTime(s.getPeriod() != null && s.getPeriod().getEndTime() != null
                        ? s.getPeriod().getEndTime().toString() : null)
                .subjectId(s.getSubject() != null ? s.getSubject().getId() : null)
                .subjectName(s.getSubject() != null ? s.getSubject().getName() : null)
                .teacherId(s.getTeacher() != null ? s.getTeacher().getId() : null)
                .teacherName(teacherName)
                .build();
    }

    private String dayName(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            case 7 -> "Sunday";
            default -> "Unknown";
        };
    }
}
