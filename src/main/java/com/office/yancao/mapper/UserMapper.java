package com.office.yancao.mapper;

import com.office.yancao.dto.admin.UserQuery;
import com.office.yancao.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    String getUserClassById(@Param("userId") Long userId);

    List<User> listUsers();

    // 根据用户班级获取所有用户的Id
    List<Long> getUserByClass(@Param("classes") String classes);

    // 根据用户党支部获取所有用户的Id
    List<Long> getUserByParty(@Param("party") String party, @Param("member") String member);

    // admin方法，根据用户传入信息查询所有用户信息
    List<User> selectUserList(UserQuery query);

    int updateUserInfo(User user);

    // 更新用户信息
}
