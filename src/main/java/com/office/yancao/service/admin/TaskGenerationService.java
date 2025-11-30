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
//    @Transactional
//    public int generateTasksForDate(LocalDate targetDate) {
//        // 获取目标日期的星期几 (1-7, 1=周一)
//        int weekday = targetDate.getDayOfWeek().getValue();
//
//        // 获取所有活跃员工
//        List<User> activeEmployees = userInfoMapper.listUsers();
//
//        List<SixsTaskInstance> instancesToCreate = new ArrayList<>();
//
//        for (User employee : activeEmployees) {
//            String classes = employee.getClasses();
//            if (classes.equals("甲班")){
//                classes = "JIA";
//            }else {
//                classes = "YI";
//            }
//            System.out.println(classes);
//            // 获取员工当天的排班
//            ShiftSchedule schedule = shiftScheduleMapper.selectByDateAndTeam(targetDate, classes);
//
//            if (schedule == null) {
//                continue; // 没有排班，跳过
//            }
//
//            // 根据岗位和班次查询对应的任务模板
//            SixsTaskTemplate templates = sixsTaskTemplateMapper.selectByGroupShiftWeekday(
//                    employee.getPosition(),
//                    schedule.getShiftType(),
//                    weekday
//            );
//
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
//
//        // 批量插入任务实例
//        if (!instancesToCreate.isEmpty()) {
//            return sixsTaskInstanceMapper.batchInsert(instancesToCreate);
//        }
//
//        return 0;
//    }

}
