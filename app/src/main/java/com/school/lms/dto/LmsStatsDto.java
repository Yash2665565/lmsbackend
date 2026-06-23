package com.school.lms.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LmsStatsDto {
    private int activeCourses;
    private long submissionsCount;
    private int pendingAssignments;
}
