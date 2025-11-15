// com.office.yancao.dto.TobaccoRecordDTO.java
package com.office.yancao.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TobaccoRecordDTO {
    private Long id;
    private String brand;
    private String batchNumber;
    private BigDecimal weight;
private LocalDateTime exitTime; // 退出时间
    private LocalDateTime addTime; // 加入时间（可从operateTime转换）
    private Boolean isTimeout;
   
     // 新增：退出关联的工单ID
    private Long exitWorkOrderId;
    
    // 新增：加入关联的工单ID
    private Long addWorkOrderId;
}