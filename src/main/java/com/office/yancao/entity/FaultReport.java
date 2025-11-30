package com.office.yancao.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FaultReport {
    private Long id;
    private Long reporterId;
    private String line;
    private String section;
    private String type; // electrical, mechanical
    private String description;
    private String status; // reported, arrived, repaired
    private LocalDateTime queryTime;
    private LocalDateTime reportTime;
    private LocalDateTime arrivalTime;
    private LocalDateTime repairTime;
    private Long repairmanId;
    private LocalDateTime createdTime;
    private Long durationMinutes;
    private  String repairNotes;

}