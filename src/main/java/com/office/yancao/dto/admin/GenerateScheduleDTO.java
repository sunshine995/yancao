package com.office.yancao.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class GenerateScheduleDTO {
    private String month;       // 月份
    private Integer startWeek; // 开始周次
    private String jiaShift;
    private String yiShift;
    private String applyToRemaining;
}