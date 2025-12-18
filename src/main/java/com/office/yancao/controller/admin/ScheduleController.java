package com.office.yancao.controller.admin;

import com.office.yancao.dto.admin.GenerateScheduleDTO;
import com.office.yancao.dto.admin.ShiftScheduleVO;
import com.office.yancao.entity.admin.ShiftSchedule;
import com.office.yancao.service.admin.ScheduleService;
import com.office.yancao.untils.Result;
import com.office.yancao.service.admin.TaskGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")

public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TaskGenerationService taskGenerationService;

    // 更新剩余本月排版
    @PostMapping("/update-remaining")
    public Result<Void> generateMonthlySchedule(@RequestBody GenerateScheduleDTO generateScheduleDTO) {
        try {
            scheduleService.updateRemainingSchedule(generateScheduleDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("生成月度排班失败");
        }
    }

    /**
     * 获取月度排班
     */
    @GetMapping("/month/{month}")
    public Result<List<ShiftScheduleVO>> getScheduleByMonth(@PathVariable String month) {
        try {
            System.out.println(month);
            List<ShiftScheduleVO> schedules = scheduleService.getScheduleByMonth(month);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.fail("获取排班数据失败");
        }
    }

    /**
     * 生成月度排班
     */
    @PostMapping("/generate-monthly")
    public Result<Void> generateMonthlySchedule() {
        try {
            //scheduleService.generateMonthlySchedules();
            taskGenerationService.generateTasksForDate(LocalDate.now());
            return Result.success();
        } catch (Exception e) {
            return Result.fail("生成月度排班失败");
        }
    }

    /**
     * 更新单日排班
     */
    @PutMapping("/daily")
    public Result<?> updateDailySchedule(@RequestBody ShiftSchedule updateDTO) {
        try {
            scheduleService.updateDailySchedule(updateDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("更新失败: " + e.getMessage());
        }
    }

}