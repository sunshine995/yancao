package com.office.yancao.dto.admin;

import lombok.Data;

@Data
public class LearningResourceQueryDTO {
    private Integer categoryId;
    private String resourceType;
    private String title;       // 标题
    private Integer pageNum = 1;   // 当前页
    private Integer pageSize = 10; // 每页条数
}
