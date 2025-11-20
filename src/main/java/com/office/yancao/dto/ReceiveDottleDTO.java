package com.office.yancao.dto;

import lombok.Data;

import java.math.BigDecimal;
// 接收掺兑烟丝
@Data
public class ReceiveDottleDTO {
    private String batchId;
    private String brandId;
    private Integer totalBags;
    private BigDecimal totalWeight;
    private String shiftType;
    private String operator;
    private String status;
    private String remark;
}
