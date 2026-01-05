package com.office.yancao.dto.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssessmentRuleExcelDTO {

    @ExcelProperty("规则编码")
    private String ruleCode;

    @ExcelProperty("考核类型")
    private String ruleType;

    @ExcelProperty("制度章节")
    private String ruleSection;

    @ExcelProperty("考核规则描述")
    private String ruleName;

    @ExcelProperty("处罚金额")
    private BigDecimal penaltyAmount;

    @ExcelProperty("处罚单位")
    private String penaltyUnit;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("状态")
    private Integer status;
}

