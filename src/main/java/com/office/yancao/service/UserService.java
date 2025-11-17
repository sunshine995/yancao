package com.office.yancao.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.LoginDTO;
import com.office.yancao.dto.RegisterDTO;
import com.office.yancao.dto.admin.UserQuery;
import com.office.yancao.entity.Department;
import com.office.yancao.entity.User;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.untils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录功能
     * @param loginDTO 登录请求参数
     * @return 登录结果，包含token和用户信息
     */
    public Map<String, Object> findByUsername(LoginDTO loginDTO) {
        Map<String, Object> result = new HashMap<>();
        // 1. 根据用户ID查询用户
        User dbUser = userMapper.getUsersById(loginDTO.getUserId());
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


        // 4. 构造返回数据
        result.put("code", "200");
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
        userInfo.put("line", dbUser.getLine());
        userInfo.put("department", dbUser.getDepartment());
        userInfo.put("party", dbUser.getParty());
        userInfo.put("member", dbUser.getMember());
        result.put("user", userInfo);
        return result;
    }

    /**
     * 用户注册功能
     * @param registerDTO 注册请求参数
     * @return 注册结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> register(RegisterDTO registerDTO) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查用户名是否已存在
        User existingUser = userMapper.getUserByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            result.put("code", 400);
            result.put("msg", "用户名已存在");
            return result;
        }

        // 2. 创建用户对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword()); // 注意：实际项目中应该对密码进行加密
        user.setPhone(registerDTO.getPhone());
        user.setPosition(registerDTO.getPosition());
        user.setRole(registerDTO.getRole());
        user.setClasses(registerDTO.getClasses());
        user.setSection(registerDTO.getSection());
        user.setParty(registerDTO.getParty());
        user.setMember(registerDTO.getMember());

        // 3. 根据departmentId查询部门名称并设置
//        if (registerDTO.getDepartmentId() != null) {
//            try {
//                // 使用UserDepartmentMapper获取部门信息（根据系统实际情况调整参数）
//                List<Department> departments = userDepartmentMapper.getDirectChildren(0L); // 使用0作为顶级部门ID
//                System.out.println("查询到的部门列表: " + departments);
//                System.out.println("查找的部门ID: " + registerDTO.getDepartmentId());
//
//                // 如果直接查询没有结果，尝试查询所有部门
//                if (departments == null || departments.isEmpty()) {
//                    // 备用方案：直接查询department表的所有部门
//                    departments = userDepartmentMapper.getDirectChildren(null);
//                    System.out.println("尝试备用查询，部门列表: " + departments);
//                }
//
//                Optional<Department> deptOptional = departments.stream()
//                        .filter(dept -> {
//                            boolean match = dept.getId().equals(registerDTO.getDepartmentId());
//                            System.out.println("检查部门: " + dept.getId() + "=" + dept.getName() + ", 匹配结果: " + match);
//                            return match;
//                        })
//                        .findFirst();
//
//                if (deptOptional.isPresent()) {
//                    user.setDepartment(deptOptional.get().getName());
//                    System.out.println("设置部门名称: " + deptOptional.get().getName());
//                } else {
//                    System.out.println("未找到匹配的部门ID: " + registerDTO.getDepartmentId());
//                    // 特殊处理：如果部门ID为1，直接设置为"制丝部门"
//                    if (registerDTO.getDepartmentId() != null && registerDTO.getDepartmentId() == 1L) {
//                        user.setDepartment("制丝部门");
//                        System.out.println("特殊处理：部门ID为1，直接设置为制丝部门");
//                    } else {
//                        // 设置默认部门名称，避免null
//                        user.setDepartment("未分配部门");
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("查询部门信息出错: " + e.getMessage());
//                // 出错时也设置默认值
//                user.setDepartment("部门信息获取失败");
//            }
//        } else {
//            // 如果没有提供departmentId，也设置默认值
//            user.setDepartment("无部门");
//        }

        // 3. 插入用户信息
        int insertResult = userMapper.insertUser(user);
        if (insertResult <= 0) {
            result.put("code", 500);
            result.put("msg", "用户注册失败");
            return result;
        }

        // 4. 插入用户部门关联关系
        if (registerDTO.getDepartmentId() != null) {
            userMapper.insertUserDepartment(user.getId(), registerDTO.getDepartmentId());
        }

        // 5. 返回成功结果
        result.put("code", 200);
        result.put("msg", "注册成功");
        result.put("userId", user.getId());

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

    public List<User> listUsers() {
        return userMapper.listUsers();
    }

    public PageInfo<User> getUserList(UserQuery query) {
        if (!query.getClasses().equals("管理组")){
            return null;
        }
        if (query.getPosition().equals("甲班班长")){
            query.setClasses("甲班");
        }else if (query.getPosition().equals("乙班班长")){
            query.setClasses("乙班");
        }else {
            query.setClasses("all");
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        System.out.println(query.getClasses());
        try {
            List<User> userList = userMapper.selectUserList(query);
            System.out.println("【查询成功】结果数量: " + (userList == null ? "null" : userList.size()));
            return new PageInfo<>(userList);
        } catch (Exception e) {
            System.out.println("【❌ 查询失败！异常如下】");
            e.printStackTrace(); // 关键！看具体报什么错
            throw e; // 或按需处理
        }

    }
}
