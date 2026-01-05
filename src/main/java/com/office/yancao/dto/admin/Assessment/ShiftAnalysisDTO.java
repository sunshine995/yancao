package com.office.yancao.dto.admin.Assessment;

import lombok.Data;

@Data
public class ShiftAnalysisDTO {
    private String shift;     // 班次名称
    private Integer count;    // 考核数量
    private String rate;      // 占比
}
