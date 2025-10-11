package com.office.yancao.service;

import com.office.yancao.dto.LoginDTO;
import com.office.yancao.entity.User;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.untils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> findByUsername(LoginDTO loginDTO) {
        Map<String, Object> result = new HashMap<>();
        User dbUser = userMapper.getUsersById(loginDTO.getUserId());
        if (dbUser == null) {
            System.out.println("用户不存在");
            result.put("code", 401);
            result.put("msg", "用户不存在");
            return result;
        }

        // 2. 验证密码（假设密码已加密存储）
        if (!verifyPassword(loginDTO.getPassword(), dbUser.getPassword())) {
            result.put("code", 401);
            result.put("msg", "密码错误");
            return result;
        }

        // 3. 生成 JWT token
        String token = jwtUtil.generateToken(dbUser.getId().toString());
        System.out.println(token);

        // 4. 构造返回数据
        result.put("code", 200);
        result.put("msg", "登录成功");
        result.put("token", token);
        result.put("userId", dbUser.getId());

        // 返回用户基本信息（去敏感字段）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", dbUser.getUsername());
        userInfo.put("role", dbUser.getRole());
        userInfo.put("avatar", dbUser.getRole());
        userInfo.put("phone", dbUser.getPhone());
        userInfo.put("position", dbUser.getPosition());
        userInfo.put("class", dbUser.getClasses());
        result.put("user", userInfo);
        return result;
    }

    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword); // ❌ 仅测试用
        // ✅ 正确做法：return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
