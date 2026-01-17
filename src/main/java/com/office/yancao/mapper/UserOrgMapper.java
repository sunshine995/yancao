package com.office.yancao.mapper;

import com.office.yancao.dto.UserClassDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserOrgMapper {

    /** 查询用户主班组 */
    UserClassDTO selectPrimaryClassByUserId(@Param("userId") Long userId, @Param("classes") String classes);

}
