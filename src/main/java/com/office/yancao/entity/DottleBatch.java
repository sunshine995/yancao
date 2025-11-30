package com.office.yancao.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 残烟丝批次记录表
@Data
public class DottleBatch {

    private Long id;


    private String batchId;


    private String brandId;


    private String brandName; // 联表查询使用


    private LocalDate receiveDate;


    private LocalDate expireDate;


    private Integer totalBags;


    private BigDecimal totalWeight;


    private Integer remainingBags;


    private BigDecimal remainingWeight;

    private String status;


    private Integer daysRemaining;


    private String shiftType;

    private String operator;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
