package com.office.yancao.mapper;

import com.office.yancao.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User getUsersById(@Param("userId") Long userId);
    /**
     * 根据用户名查询用户（用于检查用户名是否已存在）
     */
    User getUserByUsername(@Param("username") String username);

    /**
     * 插入用户信息
     */
    int insertUser(User user);

    /**
     * 插入用户部门关联关系
     */
    int insertUserDepartment(@Param("userId") Long userId, @Param("departmentId") Long departmentId);

    /**
     * 根据用户ID查询用户所在班级
     */
    String getUserClassById(@Param("userId") Integer userId);
}
