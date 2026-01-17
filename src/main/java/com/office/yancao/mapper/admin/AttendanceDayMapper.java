package com.office.yancao.mapper.admin;

import com.office.yancao.entity.admin.AttendanceDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceDayMapper {

    AttendanceDay selectByUserAndDate(@Param("userId") Long userId,
                                      @Param("date") LocalDate date);

    List<AttendanceDay> selectByGroupAndDateRange(@Param("groupName") String groupName,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    int insert(AttendanceDay attendanceDay);

    int update(AttendanceDay attendanceDay);

    int deleteByUserAndDate(@Param("userId") Long userId,
                            @Param("date") LocalDate date);
}
