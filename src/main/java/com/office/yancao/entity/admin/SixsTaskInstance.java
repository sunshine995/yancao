package com.office.yancao.entity.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SixsTaskInstance {
    private Integer id;
    private Integer templateId;
    private Long employeeId;
    private LocalDate scheduledDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedCompletionTime;

    private String status; // pending, completed, timeout
    private List<String> submittedImages; // JSON 数组
    private String completionNotes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    private String spotCheckStatus; // not_checked, passed, failed
    private Integer spotCheckerId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime spotCheckTime;

    private String spotCheckResult; // pass, fail
    private String spotCheckNotes;
    private List<String> spotCheckImages; // JSON 数组

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
