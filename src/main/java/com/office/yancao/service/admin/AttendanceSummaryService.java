package com.office.yancao.service.admin;

import com.office.yancao.mapper.admin.AttendanceDayMapper;
import com.office.yancao.mapper.admin.AttendanceEventMapper;
import com.office.yancao.mapper.admin.AttendanceMonthSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class AttendanceSummaryService {

    @Autowired
    private AttendanceDayMapper attendanceDayMapper;

    @Autowired
    private AttendanceEventMapper attendanceEventMapper;

    @Autowired
    private AttendanceMonthSummaryMapper attendanceMonthSummaryMapper;

    @Transactional // 确保计算和更新过程的一致性
    public void performDailyUpdate(String yearMonths) {
        attendanceMonthSummaryMapper.deleteByMonth(yearMonths);
        attendanceMonthSummaryMapper.insertByMonth(yearMonths);
    }

}
