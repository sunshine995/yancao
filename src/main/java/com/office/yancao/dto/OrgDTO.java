package com.office.yancao.dto;

import lombok.Data;

@Data
public class OrgDTO {

    private Long orgId;
    private String orgName;
    private String orgType;   // class / section / group / party
    private String orgRole;   // 成员 / 班长 / 主任
}

