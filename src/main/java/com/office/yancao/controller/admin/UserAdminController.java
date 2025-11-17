package com.office.yancao.controller.admin;

import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.UserQuery;
import com.office.yancao.entity.User;
import com.office.yancao.service.UserService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/userAdmin")
@RestController
public class UserAdminController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页+条件查询）
     */
    @GetMapping("/list")
    public Result<PageInfo<User>> getUserList(UserQuery query) {
        try {
            System.out.println(query.getClasses());
            PageInfo<User> pageInfo = userService.getUserList(query);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.fail("查询失败: " + e.getMessage());
        }
    }
}
