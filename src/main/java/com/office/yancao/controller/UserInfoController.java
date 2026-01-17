package com.office.yancao.controller;


import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.LoginDTO;
import com.office.yancao.dto.UserFullInfoDTO;
import com.office.yancao.dto.admin.UserQuery;
import com.office.yancao.entity.UserInfo;
import com.office.yancao.service.UserInfoService;
import com.office.yancao.service.UserService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userService;

    /**
     * 更新后用户登录接口
     * @param loginDTO 登录请求参数
     * @return 登录结果，包含token和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO){
        Map<String, Object> byUsername = userService.findByUsername(loginDTO);
        if (byUsername.get("code") == "200") {
            return Result.success(byUsername);
        }else {
            return Result.fail("用户密码或密码错误");
        }
    }

    /**
     * 查询所有班级用户
     */
    @GetMapping("/getUserByClass")
    public Result<List<UserFullInfoDTO>> getUserByClass(){
        List<UserFullInfoDTO> shifts = userService.getUserByClass("甲班");
        return Result.success(shifts);
    }

    /**
     * 分页查询所有班级用户
     */
    @GetMapping("/pageByUser")
    public Result<PageInfo<UserFullInfoDTO>> pageByUser(UserQuery query){
        PageInfo<UserFullInfoDTO> shifts = userService.pageByUser(query);
        return Result.success(shifts);
    }
}
