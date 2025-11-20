package com.office.yancao.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// 残烟丝操作记录表
@Data
public class DottleOperation {

    private Long id;

    private Long batchId;

    private String batchIdStr; // 批次号

    private String brandName; // 联表查询使用

    private Integer operationType;

    private Integer bagsUsed;

    private BigDecimal weightUsed;

    private String shiftType;

    private String operator;

    private LocalDateTime operationTime;

    private String remark;

    private LocalDateTime createTime;
}
