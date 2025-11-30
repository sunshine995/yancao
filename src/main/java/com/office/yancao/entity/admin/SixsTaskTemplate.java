package com.office.yancao.entity.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SixsTaskTemplate implements Serializable {
    private static final long serialVersionUID = 1L;


    private Integer id;

    private String templateName;

    private String groupName;

    private String shift;

    private Integer weekday;

    private String taskContent;

    private String taskStandard;

    private Integer requiredImages;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

