package com.office.yancao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String employeeId;
    private String phone;
    private String password;
    private Date birthday;
    private Integer status;
    private Date createTime;
}
