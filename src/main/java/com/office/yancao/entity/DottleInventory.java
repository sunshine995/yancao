package com.office.yancao.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//
@Data
public class DottleInventory {

    private Long id;

    private LocalDate statDate;

    private Long brandId;

    private String brandName; // 联表查询使用

    private Integer openingBags;

    private BigDecimal openingWeight;

    private Integer receiveBags;

    private BigDecimal receiveWeight;

    private Integer blendBags;

    private BigDecimal blendWeight;

    private Integer returnBags;

    private BigDecimal returnWeight;

    private Integer closingBags;

    private BigDecimal closingWeight;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
