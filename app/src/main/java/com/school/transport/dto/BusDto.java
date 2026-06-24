package com.school.transport.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusDto {
    private Long id;
    private String busNumber;
    private Long routeId;
    private String routeName;
    private Integer capacity;
    private String driverName;
    private String driverPhone;
    private String conductorName;
    private String conductorPhone;
    private String pickupTime;
    private String dropTime;
    private long assignedCount;
}
