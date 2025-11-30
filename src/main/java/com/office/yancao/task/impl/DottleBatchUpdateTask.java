package com.office.yancao.task.impl;


import com.office.yancao.service.DottleBatchService;
import com.office.yancao.task.ScheduledTaskRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DottleBatchUpdateTask implements ScheduledTaskRunner {

    private final DottleBatchService dottleBatchService;

    @Override
    public void run() {
        dottleBatchService.updateBatchStatusDaily();
    }

    @Override
    public String getTaskName() {
        return "残烟丝批次状态每日更新";
    }

    @Override
    public String getCronExpression() {
        return "0 50 0 * * ?"; // 每天 00:10 执行
    }
}
