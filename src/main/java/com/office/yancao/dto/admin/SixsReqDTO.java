package com.office.yancao.dto.admin;

import lombok.Data;

@Data
public class SixsReqDTO {
    private Integer pageNum = 1;      // 当前页码，默认第1页
    private Integer pageSize = 10;    // 每页大小，默认10条
    private String groupName;         // 分组名称
    private String shift;             // 班次
    private Integer weekday;          // 星期几
}
