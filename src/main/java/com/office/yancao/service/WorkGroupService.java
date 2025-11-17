package com.office.yancao.service;

import com.office.yancao.dto.GroupAddMemberDTO;
import com.office.yancao.dto.UserSimpleDTO;
import com.office.yancao.dto.WorkGroupDTO;
import com.office.yancao.entity.GroupUser;
import com.office.yancao.entity.User;
import com.office.yancao.entity.WorkGroup;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.mapper.WorkGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkGroupService {

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public WorkGroup create(WorkGroup group) {
        group.setType("DYNAMIC");
        group.setStatus(true);
        workGroupMapper.insert(group);
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupId(group.getId());
        groupUser.setUserId(group.getCreatorId());
        groupUser.setOperatorId(group.getCreatorId());
        workGroupMapper.insertUserGroup(groupUser);
        return group; // 此时 id 已由数据库生成并回填
    }

    public boolean updateWorkById(WorkGroup group) {
        return workGroupMapper.updateById(group) > 0;
    }

    public boolean deleteGroupById(Long id) {
        workGroupMapper.deleteByGroupId(id);
        return workGroupMapper.deleteById(id) > 0;
    }

    public WorkGroup getById(Long id) {
        return workGroupMapper.selectById(id);
    }

    public List<WorkGroupDTO> getWorkGroupByUserId(Long creatorId) {
        List<WorkGroupDTO> workGroupDTOList = new ArrayList<>();
        List<WorkGroup> groups = workGroupMapper.getWorkGroupByUserId(creatorId);
        for (WorkGroup workGroup : groups){
            WorkGroupDTO workGroupDTO = new WorkGroupDTO();
            List<GroupUser> groupUsers = workGroupMapper.getGroupUserByGroupId(workGroup.getCreatorId(), workGroup.getId());
            workGroupDTO.setId(workGroup.getId());
            workGroupDTO.setName(workGroup.getName());
            workGroupDTO.setDescription(workGroup.getDescription());
            workGroupDTO.setCreatorId(workGroup.getCreatorId());
            List<UserSimpleDTO> userSimpleDTOList = new ArrayList<>();
            for (GroupUser groupUser : groupUsers){
                UserSimpleDTO userSimpleDTO = new UserSimpleDTO();
                User usersById = userMapper.getUsersById(groupUser.getUserId());
                userSimpleDTO.setUsername(usersById.getUsername());
                userSimpleDTO.setPosition(usersById.getPosition());
                userSimpleDTO.setId(groupUser.getUserId());
                userSimpleDTOList.add(userSimpleDTO);
            }
            workGroupDTO.setMembers(userSimpleDTOList);
            workGroupDTOList.add(workGroupDTO);
        }
        // 3. 为每个组查询其所有成员的简要信息
        return workGroupDTOList;
    }

    public Boolean GroupAddMember(GroupAddMemberDTO groupAddMemberDTO) {
        if (groupAddMemberDTO.getGroupId() == null || groupAddMemberDTO.getUserIds() == null ||
                groupAddMemberDTO.getUserIds().isEmpty() || groupAddMemberDTO.getOperatorId() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 构造批量插入数据
        List<GroupUser> groupUsers = groupAddMemberDTO.getUserIds().stream()
                .map(userId -> {
                    GroupUser gu = new GroupUser();
                    gu.setGroupId(groupAddMemberDTO.getGroupId());
                    gu.setUserId(userId);
                    gu.setOperatorId(groupAddMemberDTO.getOperatorId());
                    return gu;
                })
                .collect(Collectors.toList());

        workGroupMapper.batchInsert(groupUsers);
        return  true;
    }

    public boolean removeMemberFromGroup(Long groupId, Long userId) {
        // 删除 group_user 表中 groupId 和 userId 的记录
        boolean flag = workGroupMapper.deleteByGroupIdAndUserId(groupId, userId) > 0;
        return  flag;
    }
}
