package com.office.yancao.mapper;

import com.office.yancao.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User getUsersById(@Param("userId") Long userId);
}
