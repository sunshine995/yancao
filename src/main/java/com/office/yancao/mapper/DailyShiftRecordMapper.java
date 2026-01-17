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

    // 查询箱皮结算记录（支持条件筛选，分页由PageHelper自动处理）
    List<DailyShiftRecord> selectBoxSettlementRecords(@Param("classes") String classes,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    // 查询箱皮结算记录总数（用于分页）
    int selectBoxSettlementRecordsCount(@Param("classes") String classes,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

}
