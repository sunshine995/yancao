package com.office.yancao.dto;

import lombok.Data;

import java.util.List;

@Data
public class    UserFullInfoDTO {

    /* ===== 用户基本信息 ===== */
    private Long userId;
    private String username;
    private String phone;
    private String employeeId;

    /* ===== 班级信息 ===== */
    private String className;


    /* ===== 管理组信息 ===== */
    private String mangerName;

    /* ===== 工段信息 ===== */
    private Long sectionId;
    private String sectionName;

    /* ===== 岗位信息 ===== */
    private String line;
    private Long postId;
    private String postName;
    private String supportName;    // main / assist

}
