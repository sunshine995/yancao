package com.office.yancao.dto;

import lombok.Data;

@Data
public class OrgMemberDTO {

    private Long userId;
    private String username;

    /** 在该组织中的角色：成员 / 班长 / 主任 */
    private String orgRole;

    /** 是否管理人员 */
    private boolean manager;

    /** 岗位信息（可选） */
    private PostDTO posts;
}

