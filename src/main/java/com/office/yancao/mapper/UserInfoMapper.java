package com.office.yancao.mapper;

import com.office.yancao.entity.User;
import com.office.yancao.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    int insert(User user);

    UserInfo selectById(@Param("id") Long id);

    UserInfo selectByUsername(@Param("username") String username);

    List<UserInfo> selectAll();
}
