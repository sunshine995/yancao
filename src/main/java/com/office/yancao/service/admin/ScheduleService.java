package com.office.yancao.service.admin;

import com.office.yancao.dto.admin.GenerateScheduleDTO;
import com.office.yancao.dto.admin.ShiftScheduleVO;
import com.office.yancao.entity.admin.ShiftSchedule;
import com.office.yancao.mapper.admin.ShiftScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ScheduleService {

    @Autowired
    private ShiftScheduleMapper shiftScheduleMapper;

    /**
     * 更新剩余排班
     */
    @Transactional
    public void updateRemainingSchedule(GenerateScheduleDTO updateDTO) {
        String month = updateDTO.getMonth();
        Integer startWeek = updateDTO.getStartWeek();

        System.out.println("更新剩余排班，月份：" + month + "，开始周：" + startWeek);

        // 1. 获取开始日期（从指定周的第一个周一开始）
        LocalDate startDate = getStartDateOfNaturalWeek(month, startWeek);


        // 2. 获取当前月的最后一天
        LocalDate endDateOfMonth = getEndDateOfMonth(month);

        // 3. 获取完整周结束日期（可能包含下个月初）
        LocalDate generateEndDate = getLastSundayOfMonth(endDateOfMonth);

        System.out.println("开始日期：" + startDate + "，结束日期：" + generateEndDate);

        // 3. 删除从开始日期到月末的自动排班
        deleteAutoSchedulesFromDate(startDate);

        // 4. 生成新的排班数据（只到本月最后一天）
        List<ShiftSchedule> newSchedules = generateRemainingSchedules(
                month, startWeek, updateDTO.getJiaShift(), updateDTO.getYiShift(), startDate, generateEndDate);

        // 6. 按月份分组数据
        Map<String, List<ShiftSchedule>> schedulesByMonth = groupSchedulesByMonth(newSchedules);
        // 7. 批量插入各月份的数据
        for (Map.Entry<String, List<ShiftSchedule>> entry : schedulesByMonth.entrySet()) {
            String scheduleMonth = entry.getKey();
            List<ShiftSchedule> monthSchedules = entry.getValue();

            if (!monthSchedules.isEmpty()) {
                // 删除该月份对应日期的自动排班
                deleteAutoSchedulesForMonth(scheduleMonth, monthSchedules);
                // 插入新的排班数据
                shiftScheduleMapper.batchInsert(monthSchedules);
                System.out.println("保存" + scheduleMonth + "月份数据条数：" + monthSchedules.size());
            }
        }

        System.out.println("生成排班数据条数：" + newSchedules.size());
    }

    /**
     * 按月份分组排班数据
     */
    private Map<String, List<ShiftSchedule>> groupSchedulesByMonth(List<ShiftSchedule> schedules) {
        Map<String, List<ShiftSchedule>> result = new HashMap<>();

        for (ShiftSchedule schedule : schedules) {
            LocalDate scheduleDate = schedule.getScheduleDate();
            String monthKey = scheduleDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            result.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(schedule);
        }

        return result;
    }


    /**
     * 生成剩余时间的排班
     */
    private List<ShiftSchedule> generateRemainingSchedules(String month, Integer startWeek,
                                                           String jiaShift, String yiShift,
                                                           LocalDate startDate, LocalDate endDate) {
        List<ShiftSchedule> schedules = new ArrayList<>();

        // 按周处理
        LocalDate currentWeekStart = startDate;
        String[] currentWeekShifts = {jiaShift, yiShift};
        int currentWeekNumber = startWeek;

        while (!currentWeekStart.isAfter(endDate)) {
            // 获取当前周的结束日期（周日），但不超过月末
            LocalDate currentWeekEnd = getEndOfWeek(currentWeekStart);
            if (currentWeekEnd.isAfter(endDate)) {
                currentWeekEnd = endDate;
            }

            System.out.println("处理第" + currentWeekNumber + "周: " + currentWeekStart + " 到 " + currentWeekEnd);

            // 生成当前周的所有排班（使用相同的班次配置）
            LocalDate currentDate = currentWeekStart;
            while (!currentDate.isAfter(currentWeekEnd)) {
                schedules.add(createSchedule(currentDate, "JIA", currentWeekShifts[0], false));
                schedules.add(createSchedule(currentDate, "YI", currentWeekShifts[1], false));
                currentDate = currentDate.plusDays(1);
            }

            // 准备下一周：移动到下一周的周一，并轮换班次
            currentWeekStart = currentWeekEnd.plusDays(1); // 下一周的周一
            currentWeekNumber++;

            // 轮换班次配置（只在周之间轮换，不在周内轮换）
            currentWeekShifts = rotateShifts(currentWeekShifts);
        }

        return schedules;
    }

    /**
     * 删除指定月份的自动排班数据
     */
    private void deleteAutoSchedulesForMonth(String month, List<ShiftSchedule> schedules) {
        if (schedules.isEmpty()) {
            return;
        }

        // 获取该月份数据的日期范围
        LocalDate minDate = schedules.stream()
                .map(ShiftSchedule::getScheduleDate)
                .min(LocalDate::compareTo)
                .orElse(null);
        LocalDate maxDate = schedules.stream()
                .map(ShiftSchedule::getScheduleDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        if (minDate != null && maxDate != null) {
            // 删除该日期范围内的自动排班
            List<ShiftSchedule> autoSchedules = shiftScheduleMapper.selectByDateRange(minDate, maxDate);
            for (ShiftSchedule schedule : autoSchedules) {
                shiftScheduleMapper.deleteById(schedule.getId());
            }
            System.out.println("删除" + month + "月份自动排班数据条数：" + autoSchedules.size());
        }
    }

    /**
     * 获取月度排班（显示完整的自然周，包含下个月的前几天）
     */
    public List<ShiftScheduleVO> getScheduleByMonth(String month) {
        LocalDate startDateOfMonth = LocalDate.parse(month + "-01");
        LocalDate endDateOfMonth = startDateOfMonth.withDayOfMonth(startDateOfMonth.lengthOfMonth());

        // 计算本月的第一个周一
        LocalDate firstMonday = getFirstMondayOfMonth(startDateOfMonth);

        // 如果第一个周一在下个月（即1号是周日的情况），则从1号开始显示
        LocalDate displayStartDate = firstMonday.isAfter(endDateOfMonth) ? startDateOfMonth : firstMonday;

        // 计算本月的最后一个周日（可能在下个月）
        LocalDate lastSunday = getLastSundayOfMonth(endDateOfMonth);

        // 查询从显示开始日期到最后一个周日的完整数据
        List<ShiftSchedule> schedules = shiftScheduleMapper.selectByDateRange(displayStartDate, lastSunday);

        // 转换为VO格式，包含跨月数据标记
        return convertToVOWithCrossMonth(schedules, month, displayStartDate, lastSunday);
    }


    /**
     * 获取本月的最后一个周日（可能在下个月）
     */
    private LocalDate getLastSundayOfMonth(LocalDate endDateOfMonth) {
        // 找到本月最后一天所在的周的周日
        DayOfWeek lastDayOfWeek = endDateOfMonth.getDayOfWeek();
        int daysToSunday = DayOfWeek.SUNDAY.getValue() - lastDayOfWeek.getValue();
        if (daysToSunday < 0) {
            daysToSunday += 7;
        }
        return endDateOfMonth.plusDays(daysToSunday);
    }

    /**
     * 转换为前端需要的VO格式（包含跨月数据标记）
     */
    private List<ShiftScheduleVO> convertToVOWithCrossMonth(List<ShiftSchedule> schedules, String currentMonth,
                                                            LocalDate displayStartDate, LocalDate displayEndDate) {
        Map<String, ShiftScheduleVO> voMap = new LinkedHashMap<>();
        LocalDate currentMonthStart = LocalDate.parse(currentMonth + "-01");
        LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());

        // 处理查询到的排班数据
        for (ShiftSchedule schedule : schedules) {
            LocalDate scheduleDate = schedule.getScheduleDate();

            ShiftScheduleVO vo = voMap.computeIfAbsent(scheduleDate.toString(), k -> {
                ShiftScheduleVO newVo = new ShiftScheduleVO();
                newVo.setDate(scheduleDate);
                newVo.setJiaManual(false);
                newVo.setYiManual(false);
                // 根据日期是否属于当前月，使用不同的周次计算方法
                if (scheduleDate.getMonthValue() == currentMonthStart.getMonthValue()) {
                    // 当前月日期：使用当前月的周次计算
                    newVo.setWeekOfMonth(getNaturalWeekOfMonth(scheduleDate));
                } else {

                    // 下个月日期：使用下个月的周次计算
                    int naturalWeekOfMonthForNextMonth = getNaturalWeekOfMonthForNextMonth(scheduleDate, currentMonthStart);
                    newVo.setWeekOfMonth(naturalWeekOfMonthForNextMonth);
                }
                newVo.setIsCurrentMonth(scheduleDate.getMonthValue() == currentMonthStart.getMonthValue());
                return newVo;
            });

            if ("JIA".equals(schedule.getTeam())) {
                vo.setJiaShift(schedule.getShiftType());
                vo.setJiaManual(schedule.getManual());
            } else if ("YI".equals(schedule.getTeam())) {
                vo.setYiShift(schedule.getShiftType());
                vo.setYiManual(schedule.getManual());
            }
        }


        // 确保包含完整显示范围内的所有日期（即使没有排班数据）
        ensureAllDisplayDaysIncludedWithCorrectWeek(voMap, displayStartDate, displayEndDate, currentMonth);

        // 按日期排序
        return voMap.values().stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    /**
     * 计算下个月日期的周次（相对于当前月的周次）
     */
    private int getNaturalWeekOfMonthForNextMonth(LocalDate date, LocalDate currentMonthStart) {

        // 计算当前月的总周数
        int totalWeeksOfCurrentMonth = getTotalWeeksOfMonth(currentMonthStart.toString());

        // 下个月的日期应该显示为当前月的最后一周
        return totalWeeksOfCurrentMonth;
    }

    /**
     * 计算月份的总周数
     */
    private int getTotalWeeksOfMonth(String month) {
        LocalDate startDate = LocalDate.parse(month);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LocalDate firstMonday = getFirstMondayOfMonth(startDate);
        LocalDate lastSunday = getLastSundayOfMonth(endDate);

        long weeks = ChronoUnit.WEEKS.between(firstMonday, lastSunday) + 1;
        return (int) weeks;
    }

    /**
     * 确保包含完整显示范围内的所有日期（使用正确的周次计算）
     */
    private void ensureAllDisplayDaysIncludedWithCorrectWeek(Map<String, ShiftScheduleVO> voMap,
                                                             LocalDate displayStartDate, LocalDate displayEndDate,
                                                             String currentMonth) {
        LocalDate currentMonthStart = LocalDate.parse(currentMonth + "-01");
        LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());

        // 计算当前月的总周数
        int totalWeeksOfCurrentMonth = getTotalWeeksOfMonth(currentMonthStart.toString());

        LocalDate current = displayStartDate;
        while (!current.isAfter(displayEndDate)) {
            String dateKey = current.toString();
            if (!voMap.containsKey(dateKey)) {
                ShiftScheduleVO vo = new ShiftScheduleVO();
                vo.setDate(current);

                // 设置默认班次（可以根据周次计算）
                boolean isOddWeek;
                if (current.getMonthValue() == currentMonthStart.getMonthValue()) {
                    // 当前月日期：使用当前月的周次
                    int weekOfMonth = getNaturalWeekOfMonth(current);
                    isOddWeek = weekOfMonth % 2 == 1;
                    vo.setWeekOfMonth(weekOfMonth);
                } else {
                    // 下个月日期：使用当前月的最后一周
                    isOddWeek = totalWeeksOfCurrentMonth % 2 == 1;
                    vo.setWeekOfMonth(totalWeeksOfCurrentMonth);
                }

                vo.setJiaShift(isOddWeek ? "DAY" : "MID");
                vo.setYiShift(isOddWeek ? "MID" : "DAY");
                vo.setJiaManual(false);
                vo.setYiManual(false);
                vo.setIsCurrentMonth(current.getMonthValue() == currentMonthStart.getMonthValue());
                voMap.put(dateKey, vo);
            }
            current = current.plusDays(1);
        }
    }

    /**
     * 获取指定日期所在周的结束日期（周日）
     */
    private LocalDate getEndOfWeek(LocalDate date) {
        // 找到本周的周日
        int daysToAdd = DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue();
        return date.plusDays(daysToAdd);
    }

    /**
     * 班次轮换规则
     */
    private String[] rotateShifts(String[] currentShifts) {
        // 轮换规则：交换甲班和乙班的班次
        return new String[]{currentShifts[1], currentShifts[0]};
    }

    /**
     * 获取指定周的开始日期
     */
    private LocalDate getStartDateOfNaturalWeek(String month, int weekOfMonth) {
        LocalDate firstDay = LocalDate.parse(month + "-01");
        // 计算当月第一个周一的日期
        LocalDate firstMonday = getFirstMondayOfMonth(firstDay);

        // 第1周的开始日期就是第一个周一
        if (weekOfMonth == 1) {
            return firstMonday;
        }
        // 其他周：第一个周一 + (周数-1) * 7天
        return firstMonday.plusWeeks(weekOfMonth - 1);
    }

    /**
     * 按照自然周计算周数（周一至周日）
     */
    private int getNaturalWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate firstMonday = getFirstMondayOfMonth(firstDayOfMonth);

        // 如果日期在第一个周一之前，属于第1周
        if (date.isBefore(firstMonday)) {
            return 1;
        }

        // 计算周数
        long weeksBetween = ChronoUnit.WEEKS.between(firstMonday, date);
        return (int) weeksBetween + 1;
    }

    /**
     * 获取当月的第一个周一
     */
    private LocalDate getFirstMondayOfMonth(LocalDate firstDayOfMonth) {
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

        if (firstDayOfWeek == DayOfWeek.MONDAY) {
            return firstDayOfMonth;
        } else {
            // 计算到下一个周一的距离
            int daysToAdd = DayOfWeek.MONDAY.getValue() - firstDayOfWeek.getValue();
            if (daysToAdd <= 0) {
                daysToAdd += 7;
            }
            return firstDayOfMonth.plusDays(daysToAdd);
        }
    }

    /**
     * 获取月份的最后一天
     */
    private LocalDate getEndDateOfMonth(String month) {
        LocalDate firstDay = LocalDate.parse(month + "-01");
        return firstDay.withDayOfMonth(firstDay.lengthOfMonth());
    }

    /**
     * 删除从指定日期开始的自动排班
     */
    private void deleteAutoSchedulesFromDate(LocalDate startDate) {
        // 临时实现：先查询再删除
        List<ShiftSchedule> autoSchedules = shiftScheduleMapper.selectAutoFromDate(startDate.toString());
        for (ShiftSchedule schedule : autoSchedules) {
            shiftScheduleMapper.deleteById(schedule.getId());
        }
    }

    /**
     * 创建排班记录
     */
    private ShiftSchedule createSchedule(LocalDate date, String team, String shiftType, boolean isManual) {
        ShiftSchedule schedule = new ShiftSchedule();
        schedule.setScheduleDate(date);
        schedule.setTeam(team);
        schedule.setShiftType(shiftType);
        schedule.setManual(isManual);
        return schedule;
    }

    /**
     * 生成月度排班数据（只从第一个周一开始）
     */
    public void generateMonthlySchedules() {
        // 获取当前日期
        LocalDate today = LocalDate.now();

        // 上个月
        LocalDate lastMonth = today.minusMonths(1);
        String lastMonthStr = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        // 本月
        String currentMonthStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 获取上个月最后一天
        LocalDate lastDayOfLastMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());

        // 查询上个月最后一天的排班
        List<ShiftSchedule> lastDaySchedules = shiftScheduleMapper.selectByDateRange(lastDayOfLastMonth, lastDayOfLastMonth);
        String jiaShift;
        String yiShift;

        if (lastDaySchedules.isEmpty()) {
            // 如果上个月最后一天没有排班，使用默认班次并轮换一次
            jiaShift = "MID"; // 因为默认是甲班白班，乙班中班，轮换一次后甲班中班，乙班白班
            yiShift = "DAY";
        } else {
            // 从排班数据中提取甲班和乙班的班次
            jiaShift = lastDaySchedules.stream()
                    .filter(s -> "JIA".equals(s.getTeam()))
                    .map(ShiftSchedule::getShiftType)
                    .findFirst()
                    .orElse("DAY");
            yiShift = lastDaySchedules.stream()
                    .filter(s -> "YI".equals(s.getTeam()))
                    .map(ShiftSchedule::getShiftType)
                    .findFirst()
                    .orElse("MID");

            // 轮换一次（交换）
            String temp = jiaShift;
            jiaShift = yiShift;
            yiShift = temp;
        }

        // 构建DTO
        GenerateScheduleDTO dto = new GenerateScheduleDTO();
        dto.setMonth(currentMonthStr);
        dto.setStartWeek(1);
        dto.setJiaShift(jiaShift);
        dto.setYiShift(yiShift);

        // 调用更新剩余排班方法，从第1周开始，这样会重新生成整个月的自动排班
        updateRemainingSchedule(dto);

    }

    /**
     * 更新单日排班
     */
    @Transactional
    public void updateDailySchedule(ShiftSchedule updateDTO) {
        LocalDate scheduleDate = updateDTO.getScheduleDate();
        String team = updateDTO.getTeam();
        String shiftType = updateDTO.getShiftType();
        Boolean manual = updateDTO.getManual();

        // 1. 检查是否已存在该日期的排班记录
        ShiftSchedule existingSchedule = shiftScheduleMapper.selectByDateAndTeam(scheduleDate, team);

        if (existingSchedule != null) {
            // 更新现有记录
            existingSchedule.setShiftType(shiftType);
            existingSchedule.setManual(manual != null ? manual : true);
            shiftScheduleMapper.upsertManual(existingSchedule);
        }

        System.out.println("更新单日排班: " + scheduleDate + " " + team + "班 -> " + shiftType);
    }
}