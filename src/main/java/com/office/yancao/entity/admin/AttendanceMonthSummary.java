package com.office.yancao.entity.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AttendanceMonthSummary {
    private Long id;
    private String userId; // 员工ID
    private String userName; // 员工姓名
    private String groupName; // 班组名称
    private String yearMonths; // 年月 (格式: YYYY-MM)
    private BigDecimal workDays; // 出勤天数
    private BigDecimal extendHours; // 延点小时
    private BigDecimal leaveHours; // 离岗小时
    private BigDecimal vacationHours; // 请假小时
    private BigDecimal netHours; // 净工时
    private BigDecimal carryOverHours; // 结转下月小时
    private Integer settleFlag; // 结算标志 (0-未结算, 1-已结算)
    private Date createTime;
    private Date updateTime;
}
