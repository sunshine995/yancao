package com.office.yancao.service;

import com.office.yancao.dto.WorkDTO;
import com.office.yancao.entity.Work;
import com.office.yancao.entity.admin.ShiftSchedule;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.mapper.WorkMapper;
import com.office.yancao.mapper.admin.ShiftScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class WorkService {

    @Resource
    private WorkMapper workMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShiftScheduleMapper shiftScheduleMapper;

    public boolean saveWork(WorkDTO workDTO) {
        // ✅ 处理图片上传并转换为字符串路径
        String classes = workDTO.getClasses();
        if (classes.equals("甲班班长")){
            classes = "甲班";
        }else {
            classes = "乙班";
        }

        // ✅ 创建实体对象
        Work work = new Work();
        work.setUserId(workDTO.getUserId());
        work.setClasses(classes);
        work.setLine(workDTO.getLine());
        work.setNumber(workDTO.getNumber());
        work.setBrand(workDTO.getBrand());
        work.setBatchNo(workDTO.getBatchNo());
        work.setYield(workDTO.getYield());
        work.setRemark(workDTO.getRemark());
        work.setCreateTime(LocalDateTime.now());
        List<String> images = workDTO.getImages();
        String imageStr = null;

        if (images != null && !images.isEmpty()) {
            imageStr = String.join(",", images);
        }

        work.setImages(imageStr);
        if (!(images == null) || !(work.getRemark() == null)){
            work.setIsRemark("1");
        }else {
            work.setIsRemark("0");
        }
        System.out.println(workDTO.getUserId());
        // ✅ 保存到数据库
        return workMapper.saveWork(work) > 0;
    }

    public Work getWorkById(Long id) {
        return workMapper.getWorkById(id);
    }

    public List<Work> listAllWorks() {
        return workMapper.listAllWorks();
    }

    public List<Work> listWorksByUserId(Long userId) {
        return workMapper.listWorksByUserId(userId);
    }


    public Map<String, Object> getTodayWorkOrdersByUserId(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 获取用户所在班级
        String userClass = getUserClass(userId);
        if (userClass.equals("管理组")){
            userClass = getClasses();
        }

        // 计算当天时间范围
        Date startTime = getStartTimeOfToday();
        Date endTime = getEndTimeOfToday();

        // 查询该班级的今日工单
        List<Work> workOrders = workMapper.selectTodayWorkByClass(userClass, startTime, endTime);

        // 统计今日工单总数
        int total = workMapper.countTodayWorkByClass(userClass, startTime, endTime);

        // 构建返回结果
        result.put("total", total);
        result.put("list", workOrders != null ? workOrders : Collections.emptyList());

        return result;
    }

    // 工具方法：将 LocalDateTime 转为 Date（假设使用 java.util.Date）
    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    private String getClasses(){

        // 获取当前时间（使用系统时间）
        LocalDateTime now = LocalDateTime.now();
        String currentShift; // 当前班次："甲班" 或 "乙班"
        Date startTime;
        Date endTime;

        LocalTime time = now.toLocalTime();
        if (time.compareTo(LocalTime.of(8, 0)) >= 0 && time.compareTo(LocalTime.of(16, 0)) < 0) {
            // 08:00 <= 当前时间 < 16:00 → 甲班
            ShiftSchedule day = shiftScheduleMapper.selectByDateAndDay(LocalDate.now(), "DAY");
            if (day.getTeam().equals("JIA")){
                currentShift = "甲班";
            }else {
                currentShift = "乙班";
            }
        } else if ((time.compareTo(LocalTime.of(16, 0)) >= 0 && time.compareTo(LocalTime.of(23, 59, 59)) <= 0)
                || (time.compareTo(LocalTime.MIDNIGHT) >= 0 && time.compareTo(LocalTime.of(1, 0)) <= 0)) {
            // 16:00 <= 当前时间 <= 23:59:59 或 00:00 <= 当前时间 <= 01:00 → 乙班
            ShiftSchedule day = shiftScheduleMapper.selectByDateAndDay(LocalDate.now(), "DAY");
            if (day.getTeam().equals("JIA")){
                currentShift = "甲班";
            }else {
                currentShift = "乙班";
            }

        } else {
            currentShift = "无班次";

        }
        return currentShift;
    }

    private Date getStartTimeOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndTimeOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }


    public String getUserClass(Long userId) {
        // 从user_info表查询用户所在班级
        String userClass = userMapper.getUserClassById(userId);
        // 如果查询不到，返回默认值甲班
        return userClass != null ? userClass : "甲班";
    }
}
