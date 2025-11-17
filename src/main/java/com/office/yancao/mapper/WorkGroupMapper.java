package com.office.yancao.mapper;


import com.office.yancao.entity.GroupUser;
import com.office.yancao.entity.WorkGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkGroupMapper {

    Long insert(WorkGroup workGroup);

    int updateById(WorkGroup workGroup);

    int deleteById(Long id);

    WorkGroup selectById(Long id);


    List<WorkGroup> getWorkGroupByUserId(Long creatorId);

    int batchInsert(@Param("list") List<GroupUser> list);

    List<GroupUser> getGroupUserByGroupId(Long creatorId, Long groupId);

    int deleteByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
    int deleteByGroupId(@Param("groupId") Long groupId);

    List<Long> getUsersById(@Param("groupId") Long groupId, Long creatorId);

    int insertUserGroup(GroupUser groupUser);
}
