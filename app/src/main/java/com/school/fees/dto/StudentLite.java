package com.school.fees.dto;

/** Projection for students resolved via enrollments. */
public interface StudentLite {
    Long getStudentId();
    String getStudentName();
    String getAdmissionNo();
}
