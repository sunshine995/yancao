package com.office.yancao.dto.admin;

import lombok.Data;

@Data
public class ResourceListByTypeDTO {
    private Long id;
    private Integer categoryId;
    private String resourceType;
    private String title;       // 标题
    private Long studyCount;
}
