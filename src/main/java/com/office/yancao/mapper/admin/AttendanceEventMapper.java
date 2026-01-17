package com.office.yancao.mapper.admin;

import com.office.yancao.entity.admin.AttendanceEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceEventMapper {

    /**
     * 批量插入考勤事件记录
     */
    int insertBatch(@Param("attendanceEvents") List<AttendanceEvent> attendanceEvents);

}
