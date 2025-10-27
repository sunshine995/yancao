package com.office.yancao.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class WorkDTO {
    private Long userId;
    private String classes;
    private String line;
    private String number;
    private String brand;
    private String batchNo;
    private String yield;
    private String isRemark;
    private String remark;

    // ✅ 接收多张图片
    private MultipartFile[] images; // 或者 List<MultipartFile>
}
