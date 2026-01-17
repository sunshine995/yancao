package com.office.yancao.entity.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SixsTaskTemplate implements Serializable {
    private static final long serialVersionUID = 1L;


    private Integer id;

    @ExcelProperty("6s区域")
    private String groupName;

    @ExcelProperty("星期(1-7)")
    private Integer weekday;

    @ExcelProperty("白/中")
    private String shift;

    @ExcelProperty("任务名称")
    private String templateName;


    @ExcelProperty("任务内容")
    private String taskContent;

    @ExcelProperty("任务标准")
    private String taskStandard;

    @ExcelProperty("需要拍摄几张图片")
    private Integer requiredImages;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

