package com.office.yancao.dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FaultReportDTO {
    private Long id;
    private Long reporterId;
    private String reporterName;
    private String line;
    private String section;
    private String type;
    private String description;
    private String status;
    private LocalDateTime reportTime;
    private LocalDateTime arrivalTime;
    private LocalDateTime repairTime;
    private Long repairmanId;
    private List<String> imageUrls; // 所有初始照片
    private List<String> arrivalImageUrls; // 到场照片
    private List<String> repairImageUrls; // 修复后照片
}