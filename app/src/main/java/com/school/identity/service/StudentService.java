package com.school.identity.service;

import com.school.common.exception.BadRequestException;
import com.school.common.exception.NotFoundException;
import com.school.identity.dto.StudentCreateRequest;
import com.school.identity.dto.StudentDto;
import com.school.identity.entity.Student;
import com.school.identity.entity.User;
import com.school.identity.entity.UserRole;
import com.school.identity.repository.GuardianRepository;
import com.school.identity.repository.StudentRepository;
import com.school.identity.repository.UserRepository;
import com.school.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final GuardianRepository guardianRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<StudentDto> listStudents(String search, Pageable pageable) {
        return studentRepository.searchStudents(search, pageable).map(this::toDto);
    }

    public StudentDto getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        return toDto(student);
    }

    public StudentDto create(StudentCreateRequest req) {
        if (studentRepository.existsByAdmissionNo(req.getAdmissionNo())) {
            throw new BadRequestException("Admission no already exists");
        }

        Student s = new Student();
        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setAdmissionNo(req.getAdmissionNo());
        s.setDob(req.getDob());
        s.setGender(normalizeStudentGender(req.getGender()));
        s.setPhone(req.getPhone());
        s.setAddress(req.getAddress());

        if (req.getGuardianId() != null) {
            s.setGuardian(guardianRepository.findById(req.getGuardianId()).orElse(null));
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            User u = new User();
            u.setEmail(req.getEmail());
            u.setPassword(passwordEncoder.encode(req.getPassword() != null ? req.getPassword() : "Student@123"));
            u.setName(req.getFirstName());
            u.setLname(req.getLastName());
            u.setRole("STUDENT");
            u.setStatus("active");
            u.setCreatedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            u = userRepository.save(u);

            UserRole ur = new UserRole();
            ur.setUserId(u.getId());
            ur.setRole("STUDENT");
            ur.setCreatedAt(LocalDateTime.now());
            ur.setUpdatedAt(LocalDateTime.now());
            userRoleRepository.save(ur);

            s.setUser(u);
        }

        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());
        return toDto(studentRepository.save(s));
    }

    public StudentDto update(Long id, StudentCreateRequest req) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        student.setFirstName(req.getFirstName());
        student.setLastName(req.getLastName());
        student.setAdmissionNo(req.getAdmissionNo());
        student.setDob(req.getDob());
        student.setGender(normalizeStudentGender(req.getGender()));
        student.setPhone(req.getPhone());
        student.setAddress(req.getAddress());

        if (req.getGuardianId() != null) {
            student.setGuardian(guardianRepository.findById(req.getGuardianId()).orElse(null));
        }

        student.setUpdatedAt(LocalDateTime.now());
        return toDto(studentRepository.save(student));
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    private String normalizeStudentGender(String gender) {
        if (gender == null) return null;
        return switch (gender.trim().toLowerCase()) {
            case "male", "m" -> "M";
            case "female", "f" -> "F";
            default -> "OTHER";
        };
    }

    private StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .admissionNo(s.getAdmissionNo())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .dob(s.getDob())
                .gender(s.getGender())
                .phone(s.getPhone())
                .address(s.getAddress())
                .userId(s.getUser() != null ? s.getUser().getId() : null)
                .guardianId(s.getGuardian() != null ? s.getGuardian().getId() : null)
                .guardianName(s.getGuardian() != null ? s.getGuardian().getName() : null)
                .build();
    }
}
