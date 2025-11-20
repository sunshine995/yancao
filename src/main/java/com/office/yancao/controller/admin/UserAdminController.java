package com.office.yancao.controller.admin;

import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.UserQuery;
import com.office.yancao.entity.User;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.service.UserService;
import com.office.yancao.untils.JwtUtil;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/userAdmin")
@RestController
public class UserAdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户列表（分页+条件查询）
     */
    @GetMapping("/list")
    public Result<PageInfo<User>> getUserList(UserQuery query) {
        try {
            PageInfo<User> pageInfo = userService.getUserList(query);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        }
        String userId = jwtUtil.getUserIdFromToken(token); // 调用工具类方法
        User usersById = userMapper.getUsersById(Long.valueOf(userId));
        System.out.println(usersById.getId());
        if (!usersById.getClasses().equals("管理组") && Long.valueOf(userId) != user.getId()){
            return Result.fail("没有权限修改别人信息");
        }
        try {
            System.out.println(usersById.getId());
            int i = userService.updateUserInfo(user);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

}
