package com.office.yancao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private long userId;
    private String password;
    private String remember;
}
