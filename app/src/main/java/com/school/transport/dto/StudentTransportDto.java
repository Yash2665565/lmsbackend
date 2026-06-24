package com.school.transport.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentTransportDto {
    private Long id;
    private Long studentId;
    private Long busId;
    private String pickupStop;
    // denormalised bus + route details for display
    private String busNumber;
    private String routeName;
    private String pickupTime;
    private String dropTime;
    private String driverName;
    private String driverPhone;
    private String conductorName;
    private String conductorPhone;
}
