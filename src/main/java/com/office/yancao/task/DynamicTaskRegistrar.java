package com.office.yancao.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

// 动态任务注册器
@Slf4j
@Component
public class DynamicTaskRegistrar implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private TaskScheduler taskScheduler;

    @Resource
    private List<ScheduledTaskRunner> taskRunners;

    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        registerAllTasks();
    }

    private void registerAllTasks() {
        if (taskRunners == null || taskRunners.isEmpty()) {
            log.warn("未发现任何 ScheduledTaskRunner 实现类");
            return;
        }

        for (ScheduledTaskRunner task : taskRunners) {
            try {
                String taskName = task.getTaskName();
                String cron = task.getCronExpression();

                Runnable taskRunnable = () -> {
                    try {
                        log.info("▶ 开始执行动态定时任务: {}", taskName);
                        long start = System.currentTimeMillis();
                        task.run();
                        long cost = System.currentTimeMillis() - start;
                        log.info("✅ 动态任务执行成功: {}，耗时 {} ms", taskName, cost);
                    } catch (Exception e) {
                        log.error("❌ 动态任务执行失败: {}", taskName, e);
                    }
                };

                ScheduledFuture<?> future = taskScheduler.schedule(taskRunnable, new CronTrigger(cron));
                scheduledTasks.put(taskName, future);
                log.info("⏰ 已注册动态定时任务: {}，cron = {}", taskName, cron);

            } catch (Exception e) {
                log.error("❌ 注册动态任务失败: {}", task.getTaskName(), e);
            }
        }
    }

    @PreDestroy
    public void cancelAllTasks() {
        scheduledTasks.forEach((name, future) -> {
            if (!future.isCancelled() && !future.isDone()) {
                future.cancel(true);
                log.info("⏹ 已取消动态任务: {}", name);
            }
        });
        scheduledTasks.clear();
    }
}