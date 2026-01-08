package com.office.yancao.entity.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearningResource {

    private Long id;
    private Long categoryId;
    private String title;
    private String resourceType; // VIDEO / PDF / TEXT
    private String coverUrl;
    private String content;
    private String fileUrl;
    private Integer duration; // 分钟
    private Integer status;
    private LocalDateTime createTime;
}

