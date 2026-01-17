package com.office.yancao.task.impl;

import com.office.yancao.service.admin.AttendanceSummaryService;
import com.office.yancao.task.ScheduledTaskRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.YearMonth;

@Component
public class AttendanceMonthSummaryTask implements ScheduledTaskRunner {

    @Resource
    private AttendanceSummaryService attendanceSummaryService;

    @Override
    public void run() {
        YearMonth now = YearMonth.now();
        attendanceSummaryService.performDailyUpdate(now.toString());
        attendanceSummaryService.performDailyUpdate(now.minusMonths(1).toString());
    }

    @Override
    public String getTaskName() {
        return "每日更新考勤数据";
    }

    @Override
    public String getCronExpression() {
        return  "0 50 5 * * ?";
    }
}
