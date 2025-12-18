package com.office.yancao.service.admin;

import com.office.yancao.entity.User;
import com.office.yancao.entity.admin.ShiftSchedule;
import com.office.yancao.entity.admin.SixsTaskInstance;
import com.office.yancao.entity.admin.SixsTaskTemplate;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.mapper.admin.ShiftScheduleMapper;
import com.office.yancao.mapper.admin.SixsTaskInstanceMapper;
import com.office.yancao.mapper.admin.SixsTaskTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskGenerationService {

    @Autowired
    private UserMapper userInfoMapper;

    @Autowired
    private ShiftScheduleMapper shiftScheduleMapper;

    @Autowired
    private SixsTaskTemplateMapper sixsTaskTemplateMapper;

    @Autowired
    private SixsTaskInstanceMapper sixsTaskInstanceMapper;


    /**
     * 为所有员工生成指定日期的任务
     */
    @Transactional
    public int generateTasksForDate(LocalDate targetDate) {
        // 获取目标日期的星期几 (1-7, 1=周一)
        int weekday = targetDate.getDayOfWeek().getValue();

        // 获取所有活跃员工
        List<User> activeEmployees = userInfoMapper.listUsers();


        for (User employee : activeEmployees) {
            String classes = employee.getClasses();
            if (classes.equals("甲班")) {
                classes = "JIA";
            } else {
                classes = "YI";
            }
            System.out.println(classes);
            // 获取员工当天的排班
            ShiftSchedule schedule = shiftScheduleMapper.selectByDateAndTeam(targetDate, classes);

            if (schedule == null) {
                continue; // 没有排班，跳过
            }
            String type = schedule.getShiftType();
            if (type.equals("MID")){
                type = "中班";
            }else {
                type = "白班";
            }
            // 根据岗位和班次查询对应的任务模板
            SixsTaskTemplate templates = sixsTaskTemplateMapper.selectByGroupShiftWeekday(
                    employee.getPosition(),
                    type,
                    weekday
            );

//            for (SixsTaskTemplate template : templates) {
//                // 检查是否已经生成了该任务实例
//                int existingCount = sixsTaskInstanceMapper.countByTemplateAndEmployeeAndDate(
//                        template.getId(), employee.getId(), targetDate
//                );
//
//                if (existingCount == 0) {
//                    // 创建新的任务实例
//                    SixsTaskInstance instance = createTaskInstance(template, employee, targetDate, schedule);
//                    instancesToCreate.add(instance);
//                }
//            }
//        }
            if (templates != null){
                int existingCount = sixsTaskInstanceMapper.countByTemplateAndEmployeeAndDate(
                        templates.getId(), employee.getId(), targetDate
                );
                if (existingCount == 0) {
                    // 创建新的任务实例
                    SixsTaskInstance instance = createTaskInstance(templates, employee, targetDate, schedule);
                    sixsTaskInstanceMapper.insert(instance);
                }
            }

        }
        return 0;
    }

        /**
         * 创建任务实例
         */
    private SixsTaskInstance createTaskInstance(SixsTaskTemplate template, User employee,
            LocalDate targetDate, ShiftSchedule schedule) {
        SixsTaskInstance instance = new SixsTaskInstance();
        instance.setTemplateId(template.getId());
        instance.setEmployeeId(employee.getId());
        instance.setScheduledDate(targetDate);

        // 设置计划完成时间（根据班次类型设置不同的时间）
        LocalDateTime plannedTime = calculatePlannedCompletionTime(targetDate, schedule.getShiftType());
        instance.setPlannedCompletionTime(plannedTime);

        instance.setStatus("pending");
        instance.setSpotCheckStatus("not_checked");

        return instance;
    }

    /**
     * 根据班次类型计算计划完成时间
     */
    private LocalDateTime calculatePlannedCompletionTime(LocalDate date, String shiftType) {
        LocalDateTime plannedTime;

        switch (shiftType) {
            case "白班":
                plannedTime = date.atTime(17, 0, 0); // 白班下午5点完成
                break;
            case "中班":
                plannedTime = date.plusDays(1).atTime(2, 0, 0); // 夜班次日早上8点完成
                break;
            case "夜班":
                plannedTime = date.plusDays(1).atTime(8, 0, 0); // 夜班次日早上8点完成
                break;
            default:
                plannedTime = date.atTime(18, 0, 0); // 默认下午6点完成
        }

        return plannedTime;
    }

    public void submitTask(SixsTaskInstance sixsTaskInstance) {
        // 1. 查询任务实例
        SixsTaskInstance instance = sixsTaskInstanceMapper.selectById(sixsTaskInstance.getId());
        instance.setStatus(sixsTaskInstance.getStatus());
        instance.setCompletionNotes(sixsTaskInstance.getCompletionNotes());
        instance.setSubmittedImages(sixsTaskInstance.getSubmittedImages());
        instance.setSubmittedAt(LocalDateTime.now());
        sixsTaskInstanceMapper.updateById(instance);
    }

    // 提交抽检相关任务
    public void submitSpotCheck(SixsTaskInstance sixsTaskInstance) {
        System.out.println(sixsTaskInstance.getSpotCheckResult());
        System.out.println(sixsTaskInstance.getId());
        SixsTaskInstance instance = sixsTaskInstanceMapper.selectById(sixsTaskInstance.getId());
        System.out.println(instance.getId());
        instance.setSpotCheckResult(sixsTaskInstance.getSpotCheckResult());
        instance.setSpotCheckerId(sixsTaskInstance.getSpotCheckerId());
        System.out.println(sixsTaskInstance.getSpotCheckResult());
        if (sixsTaskInstance.getSpotCheckResult().equals("pass")){
            instance.setSpotCheckStatus("passed");
        }else {
            instance.setSpotCheckStatus("failed");
        }
        instance.setSpotCheckImages(sixsTaskInstance.getSpotCheckImages());
        instance.setSpotCheckNotes(sixsTaskInstance.getSpotCheckNotes());
        instance.setSpotCheckTime(LocalDateTime.now());
        sixsTaskInstanceMapper.updateById(instance);
    }
}
