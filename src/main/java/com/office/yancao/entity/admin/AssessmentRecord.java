package com.office.yancao.entity.admin;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssessmentRecord {

    private Long id;

    // 考核类型 safety / site / process
    private String assessmentType;

    private String dept;
    private String shift;
    private String position;
    private String person;
    private LocalDate assessmentDate;

    private String content;
    private String opinion;
    private String remark;

    private String trainer;
    private String rectifyResult;
    private Integer rectifyStatus;

    private String checker;
    private String leader;

    private List<String> photoUrls;
    private String extra2;
    private String extra3;

    private String creator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Integer isDeleted;
}
