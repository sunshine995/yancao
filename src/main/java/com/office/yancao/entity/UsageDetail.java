package com.office.yancao.entity;

import lombok.Data;

@Data
public class UsageDetail {
    private Long id;
    private Long recordId;
    private String materialType; // "box" 或 "board"
    private String usageType;    // 退回西库、卷包...
    private Integer quantity;
    private String remark;
}
