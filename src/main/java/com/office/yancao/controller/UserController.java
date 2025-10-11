package com.office.yancao.controller;

import com.office.yancao.dto.LoginDTO;
import com.office.yancao.service.UserService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired UserService userService;


    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO){

        return Result.success(userService.findByUsername(loginDTO));
    }

}
