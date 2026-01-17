package com.office.yancao.mapper;

import com.office.yancao.dto.OrgDTO;
import com.office.yancao.dto.OrgMemberDTO;
import com.office.yancao.dto.PostDTO;
import com.office.yancao.dto.UserFullInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProfileMapper {

    List<OrgDTO> selectUserOrgs(@Param("userId") Long userId);

    PostDTO selectUserPosts(@Param("userId") Long userId);

    // 查询班级组成员
    List<OrgMemberDTO> selectMembersByOrg(@Param("orgId") Long orgId);

    UserFullInfoDTO selectUserFullInfo(@Param("userId") Long userId);

    List<UserFullInfoDTO> selectAllUserByClass(@Param("class") String classes);
}
