package com.office.yancao.entity.admin;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftSchedule {
    private Long id;
    private LocalDate scheduleDate;
    private String team;       // "JIA" 或 "YI"
    private String shiftType;  // "DAY" 或 "MID"
    private Boolean manual;    // true = 手动设置

    // getters & setters
}
