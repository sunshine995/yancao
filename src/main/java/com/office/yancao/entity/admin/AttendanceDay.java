package com.office.yancao.entity.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日考勤主表
 * 一人一天一班次一条记录
 */
@Data
public class AttendanceDay {

    private Long id;

    /** 用户ID（关联你已有的用户表） */
    private Long userId;

    /** 用户姓名（冗余字段，方便报表） */
    private String userName;

    /** 班组名称：甲班 / 乙班 */
    private String groupName;

    /** 考勤日期 */
    private LocalDate attendanceDate;

    /**
     * 班次类型
     * DAY-白班
     * MIDDLE-中班
     * NIGHT-晚班
     */
    private String shiftType;

    /**
     * 工作状态
     * WORK-上班
     * REST-休息
     * DUTY-值班
     */
    private String workStatus;

    /** 备注 */
    private String remark;

    /** 创建人（班长用户ID） */
    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}

