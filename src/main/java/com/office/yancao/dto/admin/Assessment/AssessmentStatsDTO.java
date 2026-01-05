package com.office.yancao.dto.admin.Assessment;

import lombok.Data;

@Data
public class AssessmentStatsDTO {
    private Integer total;    // 本月考核总数
    private Integer safety;   // 安全考核数量
    private Integer site;     // 现场考核数量
    private Integer process;  // 工艺考核数量
}
