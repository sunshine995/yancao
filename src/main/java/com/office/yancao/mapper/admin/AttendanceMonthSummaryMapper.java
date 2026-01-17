package com.office.yancao.mapper.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface AttendanceMonthSummaryMapper {

    void deleteByMonth(@Param("yearMonths") String yearMonths);

    void insertByMonth(@Param("yearMonths") String yearMonths);
}
