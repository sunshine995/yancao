package com.office.yancao.task.impl;

import com.office.yancao.service.admin.TaskGenerationService;
import com.office.yancao.task.ScheduledTaskRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SixsTask implements ScheduledTaskRunner {

    private final TaskGenerationService taskGenerationService;

    @Override
    public void run() {
        LocalDate now = LocalDate.now();
        taskGenerationService.generateTasksForDate(now);
    }

    @Override
    public String getTaskName() {
        return "每月一号更新班次状态";
    }

    @Override
    public String getCronExpression() {
        return "0 00 06 * * ?"; // 每天
    }
}
