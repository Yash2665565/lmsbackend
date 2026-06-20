package com.school.identity.service;

import com.school.common.exception.NotFoundException;
import com.school.identity.dto.TeacherCreateRequest;
import com.school.identity.dto.TeacherDto;
import com.school.identity.entity.Teacher;
import com.school.identity.entity.User;
import com.school.identity.entity.UserRole;
import com.school.identity.repository.TeacherRepository;
import com.school.identity.repository.UserRepository;
import com.school.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherDto> listAll() {
        return teacherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TeacherDto getById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher not found"));
        return toDto(teacher);
    }

    public TeacherDto create(TeacherCreateRequest req) {
        User u = new User();
        u.setName(req.getName());
        u.setLname(req.getLname());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(
                req.getPassword() != null ? req.getPassword() : "Teacher@123"));
        u.setStatus("active");
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        u = userRepository.save(u);

        UserRole userRole = new UserRole();
        userRole.setUserId(u.getId());
        userRole.setRole("subject teacher");
        userRole.setCreatedAt(LocalDateTime.now());
        userRole.setUpdatedAt(LocalDateTime.now());
        userRoleRepository.save(userRole);

        Teacher t = new Teacher();
        t.setUser(u);
        t.setEmployeeNo(req.getEmployeeNo());
        t.setQualification(req.getQualification());
        t.setJoiningDate(req.getJoiningDate());
        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());

        return toDto(teacherRepository.save(t));
    }

    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }

    private TeacherDto toDto(Teacher t) {
        return TeacherDto.builder()
                .id(t.getId())
                .userId(t.getUser() != null ? t.getUser().getId() : null)
                .name(t.getUser() != null ? t.getUser().getFullName() : null)
                .email(t.getUser() != null ? t.getUser().getEmail() : null)
                .employeeNo(t.getEmployeeNo())
                .qualification(t.getQualification())
                .joiningDate(t.getJoiningDate())
                .build();
    }
}
