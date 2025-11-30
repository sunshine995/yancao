package com.office.yancao.dto.admin;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftScheduleVO {
    private LocalDate date;        // 日期
    private String jiaShift;    // 甲班班次
    private String yiShift;     // 乙班班次
    private Boolean jiaManual;  // 甲班是否手动调整
    private Boolean yiManual;   // 乙班是否手动调整
    private Integer weekOfMonth; // 周次
    private Boolean isCurrentMonth; // 是否属于当前月份（用于区分跨月数据）
}