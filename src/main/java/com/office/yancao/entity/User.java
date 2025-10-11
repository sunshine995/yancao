package com.office.yancao.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String phone;
    private String username;
    private String password;
    private String position;
    private String role;
    private String classes;
}
