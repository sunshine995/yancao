package com.office.yancao.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfile {

    private Long userId;
    private String username;
    private String phone;

    /** 是否管理人员 */
    private boolean manager;

    /** 所属组织（班组 / 段 / 管理组 / 党支部） */
    private List<OrgDTO> orgs;

    /** 岗位信息（如果是操作工才有） */
    private PostDTO posts;
}

