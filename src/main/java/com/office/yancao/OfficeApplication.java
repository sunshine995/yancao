package com.office.yancao;


import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.office.yancao.mapper")
public class OfficeApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeApplication.class, args);
    }
}
