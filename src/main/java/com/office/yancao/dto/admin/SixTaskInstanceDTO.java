package com.office.yancao.dto.admin;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SixTaskInstanceDTO {
    private Integer id;
    private Integer templateId;
    private String templateName;

    // 执行人员信息
    private Integer employeeId;
    private String employeeName;
    private Date scheduledDate;
    private Date plannedCompletionTime;

    // 任务状态
    private String status; // pending, completed, timeout
    private String spotCheckStatus; // not_checked, passed, failed
    private String spotCheckResult; // pass, fail

    // 任务内容
    private String taskContent;
    private String taskStandard;

    // 提交信息
    private List<String> submittedImages;
    private String completionNotes;
    private Date submittedAt;

    // 抽检信息
    private Integer spotCheckerId;
    private String spotCheckerName;
    private Date spotCheckTime;
    private String spotCheckNotes;
    private List<String> spotCheckImages;

    // 模板信息
    private String groupName;
    private String shift;
    private Integer weekday;

    // 创建更新时间
    private Date createdAt;
    private Date updatedAt;
}

