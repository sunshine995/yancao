package com.office.yancao.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BoxSettlementQueryDTO {
    private String classes; // 班组（甲班/乙班）
    private LocalDate startDate; // 开始日期
    private LocalDate endDate; // 结束日期
    private Integer page; // 页码
    private Integer pageSize; // 每页大小
}