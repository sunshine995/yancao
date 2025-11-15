// com.office.yancao.entity.TobaccoRecord.java
package com.office.yancao.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // Lombok 注解，自动生成getter/setter
public class TobaccoRecord {
    private Long id;
    private String brand;
    private String batchNumber; // 对应表中 batch_number
    private Boolean isTimeout; // 对应表中 is_timeout
    private String status; // 状态：exit/added（用字符串而非枚举，简化MyBatis映射）
    private BigDecimal weight;
    private LocalDateTime operateTime; // 对应表中 operate_time
    private String submitStatus; // 提交状态：unsubmitted/submitted

    // 新增：退出时关联的工单ID（必传）
    private Long exitWorkOrderId;
    
    // 新增：加入时关联的工单ID（未加入则为null）
    private Long addWorkOrderId;
}