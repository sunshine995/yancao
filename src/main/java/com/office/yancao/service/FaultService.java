package com.office.yancao.service;


import com.office.yancao.dto.FaultReportDTO;
import com.office.yancao.dto.FaultRespList;
import com.office.yancao.entity.FaultImage;
import com.office.yancao.entity.FaultReport;
import com.office.yancao.entity.User;
import com.office.yancao.mapper.FaultImageMapper;
import com.office.yancao.mapper.FaultReportMapper;
import com.office.yancao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FaultService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.base-url}")
    private String baseUrl;

    private final FaultReportMapper faultReportMapper;
    private final FaultImageMapper faultImageMapper;
    private final UserMapper userMapper;

    public FaultService(FaultReportMapper faultReportMapper, FaultImageMapper faultImageMapper, UserMapper userMapper) {
        this.faultReportMapper = faultReportMapper;
        this.faultImageMapper = faultImageMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public Long createFaultReport(FaultReportDTO dto, List<MultipartFile> images) throws IOException {
        // 1. 保存故障
        FaultReport fault = new FaultReport();
        fault.setReporterId(dto.getReporterId());
        fault.setLine(dto.getLine());
        fault.setSection(dto.getSection());
        fault.setType(dto.getType());
        fault.setDescription(dto.getDescription());
        fault.setStatus("reported");
        fault.setReportTime(LocalDateTime.now());
        fault.setCreatedTime(LocalDateTime.now());


        faultReportMapper.insert(fault);


        // 2. 保存图片
        if (images != null && !images.isEmpty()) {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadPath + fileName);
                    Files.copy(file.getInputStream(), path);

                    FaultImage image = new FaultImage();
                    image.setFaultId(fault.getId());
                    image.setImageUrl(baseUrl + fileName);
                    image.setImageType("initial");
                    faultImageMapper.insert(image);
                }
            }
        }

        return fault.getId();
    }

    @Transactional
    public boolean markAsArrived(Long id, String status, List<MultipartFile> images) throws IOException{
        boolean one;
        if ("progress".equals(status)){
            one = faultReportMapper.updateToArrived(id, status) > 0;
        }else {
            one = faultReportMapper.updateToRepaired(id, status) > 0;
        }

        // 2. 保存图片
        if (images != null && !images.isEmpty()) {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadPath + fileName);
                    Files.copy(file.getInputStream(), path);

                    FaultImage image = new FaultImage();
                    image.setFaultId(id);
                    image.setImageUrl(baseUrl + fileName);
                    image.setImageType(status);
                    faultImageMapper.insertNoticeImage(image);
                }
            }
        }

        return one;
    }

//    @Transactional
//    public boolean markAsRepaired(Long id) {
//        return faultReportMapper.updateToRepaired(id) > 0;
//    }

    public FaultReportDTO getFaultDetail(Long id) {
        FaultReport fault = faultReportMapper.findById(id);
        if (fault == null) return null;

        FaultReportDTO dto = new FaultReportDTO();
        dto.setId(fault.getId());
        dto.setLine(fault.getLine());
        dto.setReporterId(fault.getReporterId());
        dto.setSection(fault.getSection());
        dto.setType(fault.getType());
        dto.setDescription(fault.getDescription());
        dto.setStatus(fault.getStatus());
        dto.setReportTime(fault.getReportTime());
        dto.setArrivalTime(fault.getArrivalTime());
        dto.setRepairTime(fault.getRepairTime());
        dto.setRepairmanId(fault.getRepairmanId());
        if (fault.getReporterId() != null) {
            User user = userMapper.getUsersById(fault.getReporterId());
            if (user != null) {
                dto.setReporterName(user.getUsername()); // 设置从 User 表查到的名字
            } else {
                // 如果根据 reporterId 没有找到用户，可以设置默认值或留空
                dto.setReporterName("未知用户");
                // 或者 respItem.setReporterName(null);
            }
        } else {
            // 如果 reporterId 本身就是 null，可以设置默认值或留空
            dto.setReporterName("未指定");
            // 或者 respItem.setReporterName(null);
        }

        // 查询图片
        dto.setImageUrls(faultImageMapper.findByFaultIdAndType(id, "initial"));
        dto.setArrivalImageUrls(faultImageMapper.findByFaultIdAndType(id, "progress"));
        dto.setRepairImageUrls(faultImageMapper.findByFaultIdAndType(id, "repaired"));

        return dto;
    }

    public List<FaultRespList> faultListByType(String type, Long userId) {
        List<FaultRespList> respLists = new ArrayList<>();
        List<FaultReport> faultReports = new ArrayList<>();
        if ("admin".equals(type)){
            faultReports = faultReportMapper.selectByAdmin();
        }else if ("user".equals(type)){
            faultReports = faultReportMapper.selectByUserId(userId);
        }else {
            faultReports = faultReportMapper.selectByType(type);
        }
        if (faultReports != null && !faultReports.isEmpty()){
            for (FaultReport faultReport : faultReports){
                FaultRespList respItem = new FaultRespList();
                // 4. 复制原始数据
                respItem.setId(faultReport.getId());
                respItem.setReporterId(faultReport.getReporterId());
                // 注意：FaultReport 中的 repairType 对应 FaultRespList 的 type
                respItem.setType(faultReport.getType());
                respItem.setLine(faultReport.getLine());
                respItem.setSection(faultReport.getSection());
                respItem.setDescription(faultReport.getDescription());
                respItem.setStatus(faultReport.getStatus());
                respItem.setReportTime(faultReport.getReportTime());
                Long reporterId = faultReport.getReporterId();
                if (reporterId != null) {
                    User user = userMapper.getUsersById(reporterId);
                    if (user != null) {
                        respItem.setReporterName(user.getUsername()); // 设置从 User 表查到的名字
                    } else {
                        // 如果根据 reporterId 没有找到用户，可以设置默认值或留空
                        respItem.setReporterName("未知用户");
                        // 或者 respItem.setReporterName(null);
                    }
                } else {
                    // 如果 reporterId 本身就是 null，可以设置默认值或留空
                    respItem.setReporterName("未指定");
                    // 或者 respItem.setReporterName(null);
                }
                respLists.add(respItem);
            }

        }
        return respLists;
    }
}