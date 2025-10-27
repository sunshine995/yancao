package com.office.yancao.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String phone;
    private String username;
    private String password;
    private String role;
    // 部门
    private String department;
    // 班级
    private String classes;
    // AB线
    private String line;
    // 所属段
    private String section;
    // 岗位名称
    private String position;
    //党支部名称
    private String party;
    //党小组名称
    private String member;


}
