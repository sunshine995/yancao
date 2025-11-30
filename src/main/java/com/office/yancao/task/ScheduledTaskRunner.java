package com.office.yancao.task;

// 通用任务接口
public interface ScheduledTaskRunner {
    void run();
    String getTaskName();
    String getCronExpression(); // 支持动态 cron
}
