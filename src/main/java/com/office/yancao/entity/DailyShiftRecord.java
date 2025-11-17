package com.office.yancao.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyShiftRecord {
    private Long id;
    private LocalDate recordDate;
    private String classes; // 甲班乙班

    private Integer productionBox;
    private Integer productionBoard;

    private Integer surplusBox;
    private Integer surplusBoard;

    private Integer yesterdaySurplusBox;
    private Integer yesterdaySurplusBoard;

    private LocalDate createdTime;
    private Long createdId;
}
