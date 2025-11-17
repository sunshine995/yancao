package com.office.yancao.service;

import com.office.yancao.dto.WorkDTO;
import com.office.yancao.entity.Work;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.mapper.WorkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WorkService {

    // ✅ 使用配置文件中的路径
    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.base-url}")
    private String baseUrl;

    @Resource
    private WorkMapper workMapper;

    @Autowired
    private UserMapper userMapper;

    public boolean saveWork(WorkDTO workDTO) {
        // ✅ 处理图片上传并转换为字符串路径

        // ✅ 创建实体对象
        Work work = new Work();
        work.setUserId(workDTO.getUserId());
        work.setClasses(workDTO.getClasses());
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
