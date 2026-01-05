package com.office.yancao.dto.admin.Assessment;

import lombok.Data;
// - 类型分布数据

@Data
public class TypeDistributionDTO {
    private String name;      // 考核类型名称
    private Integer value;    // 数量
    private String percentage; // 百分比
}
