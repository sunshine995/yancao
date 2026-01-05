package com.office.yancao.dto.admin;

import lombok.Data;

@Data
public class AssessmentRuleQueryDTO {

    private String ruleType;      // PROCESS / SITE / EQUIPMENT
    private String ruleSection;   // 6.1.3
    private String keyword;       // 规则关键字
    private Integer status;       // 1 / 0
    private Integer pageNum = 1;   // 当前页
    private Integer pageSize = 10; // 每页条数
}
