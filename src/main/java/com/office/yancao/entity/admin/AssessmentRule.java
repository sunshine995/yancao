package com.office.yancao.entity.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssessmentRule {

    private Long id;

    private String ruleCode;

    private String ruleType;

    private String ruleSection;

    private String ruleName;

    private BigDecimal penaltyAmount;

    private String penaltyUnit;

    private String remark;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
