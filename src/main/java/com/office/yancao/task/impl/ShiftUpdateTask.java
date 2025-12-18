package com.office.yancao.task.impl;

import com.office.yancao.service.admin.ScheduleService;
import com.office.yancao.task.ScheduledTaskRunner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShiftUpdateTask implements ScheduledTaskRunner {

    private final ScheduleService scheduledService;

    @Override
    public void run() {
        scheduledService.generateMonthlySchedules();
    }

    @Override
    public String getTaskName() {
        return "每月一号更新班次状态";
    }

    @Override
    public String getCronExpression() {
        return "0 36 10 1 * ?"; // 每月一号5点执行
    }
}
