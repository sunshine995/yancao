package com.office.yancao.mapper;

import com.office.yancao.entity.DailyShiftRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DailyShiftRecordMapper {

    int insert(DailyShiftRecord record);

    // 查询上一次的余量
    DailyShiftRecord selectLatestBefore(@Param("beforeDate") LocalDateTime beforeDate);

    DailyShiftRecord selectTargetDate(@Param("targetDate") LocalDateTime targetDate);

    List<DailyShiftRecord> selectByDate(LocalDate date);

}
