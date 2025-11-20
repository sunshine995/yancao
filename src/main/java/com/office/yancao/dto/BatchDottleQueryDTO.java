package com.office.yancao.dto;

import lombok.Data;

import java.time.LocalDate;

// 批次查询残烟丝
@Data
public class BatchDottleQueryDTO {
    private Long brandId;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
