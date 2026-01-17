package com.office.yancao.dto;


import lombok.Data;

@Data
public class UserClassDTO {

    /** 用户ID */
    private Long userId;

    /** 用户姓名 */
    private String username;

    /** 工号 */
    private String employeeId;

    /** 班组ID */
    private Long orgId;

    /** 班组名称 */
    private String orgName;

    /** 组织类型（class / group / section） */
    private String orgType;

    /** 在该班组中的角色（member / leader） */
    private String orgRole;

    /** 是否主班组 */
    private Integer isPrimary;
}
