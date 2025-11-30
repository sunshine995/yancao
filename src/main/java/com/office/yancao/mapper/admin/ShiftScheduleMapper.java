package com.office.yancao.mapper.admin;

import com.office.yancao.entity.admin.ShiftSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ShiftScheduleMapper {

    // 查询某天的排班（甲班+乙班）
    List<ShiftSchedule> selectByDate(@Param("date") LocalDate date);

    // 检查某天是否已有手动排班
    boolean existsManualRecord(@Param("date") LocalDate date);

    // 插入或更新（用于手动设置）
    void upsertManual(@Param("record") ShiftSchedule record);

    // 批量插入自动生成的排班（跳过已存在的）
    void batchInsertAuto(List<ShiftSchedule> list);

    /**
     * 批量插入排班
     */
    int batchInsert(List<ShiftSchedule> schedules);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);

    ShiftSchedule selectByDateAndTeam(@Param("date") LocalDate date, @Param("team") String team);


    /**
     * 查询从指定日期开始的自动排班
     */
    List<ShiftSchedule> selectAutoFromDate(@Param("startDate") String startDate);

    List<ShiftSchedule> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //ShiftSchedule findByClassAndDate(String classes, LocalDate targetDate);


}