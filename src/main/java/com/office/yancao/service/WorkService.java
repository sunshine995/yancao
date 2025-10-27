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
        String imagePaths = handleImageUpload(workDTO.getImages());

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
        work.setImages(imagePaths); // ✅ 设置图片路径字符串
        work.setCreateTime(LocalDateTime.now());
        if (!(imagePaths == null) || !(work.getRemark() == null)){
            work.setIsRemark("1");
        }else {
            work.setIsRemark("0");
        }
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

    /**
     * 处理图片上传，将 MultipartFile[] 转换为图片路径字符串
     */
    private String handleImageUpload(MultipartFile[] images) {
        if (images == null || images.length == 0) {
            return null; // 或者返回空字符串 ""
        }

        StringBuilder imagePaths = new StringBuilder();

        try {
            // ✅ 确保上传目录存在
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];
                if (image != null && !image.isEmpty()) {
                    // ✅ 生成唯一文件名
                    String originalFilename = image.getOriginalFilename();
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;

                    // ✅ 构建文件路径
                    Path path = Paths.get(uploadPath + uniqueFileName);

                    // ✅ 保存文件
                    Files.copy(image.getInputStream(), path);

                    // ✅ 构建访问URL
                    String imageUrl = baseUrl + uniqueFileName;

                    // ✅ 添加到路径字符串（用逗号分隔）
                    if (i > 0) {
                        imagePaths.append(",");
                    }
                    imagePaths.append(imageUrl);
                }
            }

        } catch (IOException e) {
            // ✅ 记录错误日志
            System.err.println("图片上传失败: " + e.getMessage());
            e.printStackTrace();
            // 这里可以考虑抛出业务异常或返回null
            return null;
        }

        return imagePaths.length() > 0 ? imagePaths.toString() : null;
    }

    public Map<String, Object> getTodayWorkOrdersByUserId(Integer userId) {
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


    public String getUserClass(Integer userId) {
        // 从user_info表查询用户所在班级
        String userClass = userMapper.getUserClassById(userId);
        // 如果查询不到，返回默认值甲班
        return userClass != null ? userClass : "甲班";
    }
}
