package com.office.yancao.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.LoginDTO;
import com.office.yancao.dto.OrgDTO;
import com.office.yancao.dto.UserClassDTO;
import com.office.yancao.dto.UserFullInfoDTO;
import com.office.yancao.dto.admin.UserQuery;
import com.office.yancao.entity.User;
import com.office.yancao.entity.UserInfo;
import com.office.yancao.mapper.UserInfoMapper;
import com.office.yancao.mapper.UserOrgMapper;
import com.office.yancao.mapper.UserProfileMapper;
import com.office.yancao.untils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserOrgMapper userOrgMapper;

    @Autowired
    private JwtUtil jwtUtil;


    public Map<String, Object> findByUsername(LoginDTO loginDTO) {

        Map<String, Object> result = new HashMap<>();
        // 1. 根据用户ID查询用户
        UserInfo dbUser = userInfoMapper.selectById(loginDTO.getUserId());
        if (dbUser == null) {
            System.out.println("用户不存在");
            result.put("code", "401");
            result.put("msg", "用户不存在");
            return result;
        }

        // 2. 验证密码（假设密码已加密存储）
        if (!verifyPassword(loginDTO.getPassword(), dbUser.getPassword())) {
            result.put("code", "401");
            result.put("msg", "密码错误");
            return result;
        }

        // 3. 生成 JWT token
        String token = jwtUtil.generateToken(dbUser.getId().toString());

        UserFullInfoDTO userFullInfoDTO = userProfileMapper.selectUserFullInfo(dbUser.getId());

        UserClassDTO orgInfo = userOrgMapper.selectPrimaryClassByUserId(dbUser.getId(), "class");
        if (orgInfo == null) {
            // 班级不存在，退回查班组
            orgInfo = userOrgMapper.selectPrimaryClassByUserId(dbUser.getId(), "group");
        }
        if (orgInfo != null){
            userFullInfoDTO.setClassName(orgInfo.getOrgName());
            userFullInfoDTO.setMangerName(orgInfo.getOrgRole());
        }

        // 4. 构造返回数据
        result.put("code", "200");
        result.put("msg", "登录成功");
        result.put("token", token);
        result.put("userId", dbUser.getId());
        result.put("userInfo", userFullInfoDTO);

        return result;
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 存储的密码
     * @return 验证结果
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword); // ❌ 仅测试用
        // ✅ 正确做法：return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    public List<UserFullInfoDTO> getUserByClass(String shift) {
        return userProfileMapper.selectAllUserByClass(shift);
    }

    public PageInfo<UserFullInfoDTO> pageByUser(UserQuery userQuery) {
        PageHelper.startPage(userQuery.getPageNum(), userQuery.getPageSize());
        List<UserFullInfoDTO> userFullInfoDTOS = userProfileMapper.selectAllUserByClass(userQuery.getClasses());
        return new PageInfo<>(userFullInfoDTOS);

    }
}
