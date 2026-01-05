package com.office.yancao.dto.admin;

import lombok.Data;

@Data
public class ViolationNoticeDTO {

    private String dept;
    private String shift;
    private String process;
    private String person;
    private String time;
    private String workshop;

    private String violationReason;
    private String assessmentOpinion;
    private String remark;

    private String trainer;
    private String personSign;
    private String rectifyResult;
    private String checkResult;
    private String checker;
    private String violationChecker;
    private String leader;
}
