package com.office.yancao.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class NoticeRespDto {
    private Long id;
    private String title;
    private String content;
    private String createdBy;
    private Date createdAt;
    private List<String> imageUrls; // 所有初始照片
    private int isRead;
}
