package com.office.yancao.controller;

import com.office.yancao.dto.LoginDTO;

import com.office.yancao.dto.RegisterDTO;
import com.office.yancao.service.UserService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户控制器
 * 处理用户登录和注册等功能
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginDTO 登录请求参数
     * @return 登录结果，包含token和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO){
        return Result.success(userService.findByUsername(loginDTO));
    }

    /**
     * 用户注册接口
     * 匿名访问，无需Token鉴权
     * @param registerDTO 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterDTO registerDTO){
        Map<String, Object> result = userService.register(registerDTO);
        if ((int)result.get("code") == 200) {
            return Result.success(result);
        } else {
            return Result.fail((int)result.get("code"), (String)result.get("msg"));
        }
    }
}

