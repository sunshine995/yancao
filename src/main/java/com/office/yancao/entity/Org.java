package com.office.yancao.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Org {

    private Long id;

    /** 组织名称 */
    private String orgName;

    /** workshop / group / class / section / party */
    private String orgType;

    /** 上级组织 */
    private Long parentId;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;
}
