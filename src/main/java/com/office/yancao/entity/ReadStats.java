package com.office.yancao.entity;

import lombok.Data;

@Data
public class ReadStats {
    private Integer totalCount;   // 总接收人数
    private Integer readCount;    // 已读人数
}
