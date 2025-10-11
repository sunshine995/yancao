package com.office.yancao.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String createdBy;
    private Date createdAt;
    private Boolean isDeleted;
    private List<String> images;
}
