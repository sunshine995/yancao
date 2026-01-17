package com.office.yancao.entity.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 考勤事件表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceEvent {

    private Long id;

    @ExcelProperty("员工ID")
    private Long userId;

    @ExcelProperty("员工姓名")
    private String userName;

    @ExcelProperty("班组")
    private String groupName;

    @ExcelProperty("考勤日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String attendanceDate; // 或者用 LocalDate

    @ExcelProperty("事件类型")
    private String eventType;

    @ExcelProperty("白班/中班")
    private String shiftType;

    @ExcelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ExcelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ExcelProperty("持续小时数")
    private Double durationHours;

    @ExcelProperty("创建人ID")
    private Long createBy;

    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ExcelProperty("备注")
    private String remark;
}
