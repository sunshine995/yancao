package com.office.yancao.controller.admin;

import com.office.yancao.dto.admin.AttendanceReportDTO;
import com.office.yancao.entity.admin.AttendanceDay;
import com.office.yancao.entity.admin.AttendanceEvent;
import com.office.yancao.service.admin.AttendanceDayService;
import com.office.yancao.untils.ExportExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendanceDay")
@RequiredArgsConstructor
public class AttendanceDayController {

    private final AttendanceDayService service;

    /**
     * 查询某周考勤
     */
    @GetMapping("/week")
    public List<AttendanceDay> week(@RequestParam String groupName,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate) {
        return service.queryByWeek(
                groupName,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
    }

    /**
     * 批量保存（一周）正常出勤数据
     */
    @PostMapping("/batchEvent")
    public void batchEventSave(@RequestBody List<AttendanceEvent> list) {
        service.batchEvent(list);
    }

    /**
     * 批量保存（一周）正常出勤数据
     */
    @PostMapping("/batch")
    public void batchSave(@RequestBody List<AttendanceDay> list) {
        service.saveOrUpdateBatch(list);
    }

    /**
     * 单人单天修改
     */
    @PostMapping("/update")
    public void update(@RequestBody AttendanceDay attendanceDay) {
        service.updateSingle(attendanceDay);
    }

    /**
     * 导出考勤报表
     */
    @GetMapping("/export")
    public void exportAttendanceReport(@RequestParam String groupName,
                                      @RequestParam String startDate,
                                      @RequestParam String endDate,
                                      HttpServletResponse response) {
        try {
            // 转换日期格式
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            // 生成考勤报表数据
            List<AttendanceReportDTO> reportList = service.generateAttendanceReport(groupName, start, end);
            
            // 生成日期列表
            List<LocalDate> dateList = new ArrayList<>();
            LocalDate currentDate = start;
            while (!currentDate.isAfter(end)) {
                dateList.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
            
            // 转换数据格式
            List<List<Object>> dataList = new ArrayList<>();
            int index = 1;
            for (AttendanceReportDTO report : reportList) {
                List<Object> rowData = new ArrayList<>();
                rowData.add(index++);
                rowData.add(report.getUserName());
                rowData.add(report.getGroupName());
                
                for (LocalDate date : dateList) {
                    rowData.add(report.getAttendanceByDate(date));
                }
                
                // 添加出勤天数和中班天数
                rowData.add(report.getAttendanceDays());
                rowData.add(report.getMiddleShiftDays());
                
                dataList.add(rowData);
            }
            
            // 导出Excel
            String fileName = groupName + "_考勤报表_" + startDate + "_" + endDate;
            ExportExcelUtil.exportAttendanceExcel(response, fileName, "考勤报表", dataList, dateList);
        } catch (Exception e) {
            throw new RuntimeException("导出考勤报表失败: " + e.getMessage());
        }
    }
}
