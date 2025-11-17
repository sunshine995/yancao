package com.office.yancao.dto;

import lombok.Data;

@Data
public class UsageItem {
    private String type;      // 使用类型
    private Integer quantity;
    private String remark;
}
