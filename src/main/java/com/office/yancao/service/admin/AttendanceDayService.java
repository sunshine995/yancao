package com.office.yancao.service.admin;

import com.office.yancao.dto.admin.AttendanceReportDTO;
import com.office.yancao.entity.admin.AttendanceDay;
import com.office.yancao.entity.admin.AttendanceEvent;
import com.office.yancao.mapper.admin.AttendanceDayMapper;
import com.office.yancao.mapper.admin.AttendanceEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceDayService {

    @Resource
    private AttendanceDayMapper mapper;

    @Resource
    private AttendanceEventMapper attendanceEventMapper;

    public List<AttendanceDay> queryByWeek(String groupName,
                                           LocalDate startDate,
                                           LocalDate endDate) {
        return mapper.selectByGroupAndDateRange(groupName, startDate, endDate);
    }

    @Transactional
    public void saveOrUpdateBatch(List<AttendanceDay> list) {

        for (AttendanceDay day : list) {
            AttendanceDay exist = mapper.selectByUserAndDate(
                    day.getUserId(), day.getAttendanceDate());

            day.setCreateBy(day.getCreateBy());

            if (exist == null) {
                mapper.insert(day);
            } else {
                mapper.update(day);
            }
        }
    }

    public void updateSingle(AttendanceDay attendanceDay) {
        mapper.update(attendanceDay);
    }

    public void batchEvent(List<AttendanceEvent> list) {
        attendanceEventMapper.insertBatch(list);
    }

    /**
     * 生成考勤报表数据
     * @param groupName 班组名称
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考勤报表数据列表
     */
    // 班别类型映射：英文 -> 中文
    private static final Map<String, String> SHIFT_TYPE_MAPPING = new HashMap<>();
    
    static {
        SHIFT_TYPE_MAPPING.put("DAY", "白班");
        SHIFT_TYPE_MAPPING.put("MIDDLE", "中班");
        SHIFT_TYPE_MAPPING.put("NIGHT", "夜班");
    }
    
    public List<AttendanceReportDTO> generateAttendanceReport(String groupName, LocalDate startDate, LocalDate endDate) {
        // 1. 查询指定班组和日期范围的考勤记录
        List<AttendanceDay> attendanceDays = mapper.selectByGroupAndDateRange(groupName, startDate, endDate);
        
        // 2. 按用户分组整理考勤数据
        Map<Long, AttendanceReportDTO> userReportMap = new HashMap<>();
        
        for (AttendanceDay day : attendanceDays) {
            Long userId = day.getUserId();
            AttendanceReportDTO reportDTO = userReportMap.computeIfAbsent(userId, id -> {
                AttendanceReportDTO newDTO = new AttendanceReportDTO();
                newDTO.setUserId(id);
                newDTO.setUserName(day.getUserName());
                newDTO.setGroupName(day.getGroupName());
                return newDTO;
            });
            
            // 获取班别类型并转换为中文
            String shiftType = day.getShiftType();
            String chineseShiftType = SHIFT_TYPE_MAPPING.getOrDefault(shiftType, shiftType);
            // 添加考勤记录
            reportDTO.addAttendance(day.getAttendanceDate(), chineseShiftType);
        }
        
        // 3. 将Map转换为List并返回
        return new ArrayList<>(userReportMap.values());
    }
}
