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
    private String images;
    private String filesUrl;
    private String filesName;
    private String videoUrl;

    // 返回给前端的
    private List<String> imagesUrl;
    private List<String> fileUrls;
    private List<String> fileOriginUrls;
    private List<String> videoUrls;

}
