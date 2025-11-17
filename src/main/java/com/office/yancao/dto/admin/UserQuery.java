package com.office.yancao.dto.admin;

import lombok.Data;

@Data
public class UserQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String position;
    private String classes;
    private String role;
    private String username;
}
