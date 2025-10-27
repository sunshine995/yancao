package com.office.yancao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户注册数据传输对象
 * 包含注册接口所需的所有字段
 */
@Getter
@Setter
public class RegisterDTO {
    private String username;    // 用户名
    private String password;    // 密码
    private String phone;       // 手机号
    private Long departmentId;  // 部门ID
    private String classes;     // 班次
    private String section;     // 工段
    private String position;    // 职位
    private String party;       // 党支部
    private String member;      // 党员组
    private String role;        // 角色
}