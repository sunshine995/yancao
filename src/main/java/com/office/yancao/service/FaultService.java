package com.office.yancao.service;


import com.office.yancao.dto.FaultReportDTO;
import com.office.yancao.dto.FaultRespList;
import com.office.yancao.dto.FaultUpDto;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FaultService {

    private final FaultReportMapper faultReportMapper;
    private final FaultImageMapper faultImageMapper;
    private final UserMapper userMapper;

    public FaultService(FaultReportMapper faultReportMapper, FaultImageMapper faultImageMapper, UserMapper userMapper) {
        this.faultReportMapper = faultReportMapper;
        this.faultImageMapper = faultImageMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public Long createFaultReport(FaultReportDTO dto) throws IOException {
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


        for (String image : dto.getImageUrls()){
            FaultImage faultImage = new FaultImage();
            faultImage.setFaultId(fault.getId());
            faultImage.setImageUrl(image);
            faultImage.setImageType("initial");
            faultImageMapper.insert(faultImage);
        }

        return fault.getId();
    }

    @Transactional
    public boolean markAsArrived(FaultUpDto faultUpDto) throws IOException{
        boolean one;
        if ("progress".equals(faultUpDto.getStatus())){
            one = faultReportMapper.updateToArrived(faultUpDto.getFaultId(), faultUpDto.getStatus()) > 0;
        }else if("acknowledged".equals(faultUpDto.getStatus())){
            one = faultReportMapper.updateToGet(faultUpDto.getFaultId(), faultUpDto.getStatus()) > 0;
        }else {
            FaultReport byId = faultReportMapper.findById(faultUpDto.getFaultId());

            // 确保 arrivalTime 不为 null
            if (byId.getArrivalTime() == null) {
                throw new IllegalArgumentException("到达现场时间不能为空");
            }

            // 计算持续时间（单位：分钟）
            long durationMinutes = Duration.between(byId.getArrivalTime(), LocalDateTime.now()).toMinutes();

            // 更新状态 + 持续时间（假设你的 updateToRepaired 支持传入 duration）
             one = faultReportMapper.updateToRepaired(faultUpDto.getFaultId(), faultUpDto.getStatus(), durationMinutes, faultUpDto.getRepairNotes()) > 0;
        }

        // 2. 保存图片
        List<String> images = faultUpDto.getImages();
        if (images != null && !images.isEmpty()) {
            for (String image : images) {
                FaultImage faultImage = new FaultImage();
                faultImage.setFaultId(faultUpDto.getFaultId());
                faultImage.setImageUrl(image);
                faultImage.setImageType(faultUpDto.getStatus());
                faultImageMapper.insert(faultImage);
            }
        }

        return one;
    }


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
        dto.setDurationMinutes(fault.getDurationMinutes());
        dto.setRepairNotes(fault.getRepairNotes());
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

    public List<FaultRespList> faultListByType(String type, String faultType, Long userId) {
        List<FaultRespList> respLists = new ArrayList<>();
        List<FaultReport> faultReports = new ArrayList<>();
        if ("admin".equals(type)){
            faultReports = faultReportMapper.selectByType(faultType);
        }else if ("user".equals(type)){
            faultReports = faultReportMapper.selectByUserId(userId, faultType);
        }else {
            System.out.println(type);
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

    @Transactional
    public boolean markAdminArrived(FaultUpDto faultUpDto) throws IOException{
        Boolean bo;
        if ("progress".equals(faultUpDto.getStatus())){
            bo = faultReportMapper.updateToGet(faultUpDto.getFaultId(), faultUpDto.getStatus()) > 0;
        }else {

            FaultReport byId = faultReportMapper.findById(faultUpDto.getFaultId());
            // 确保 arrivalTime 不为 null
            if (byId.getQueryTime() == null) {
                throw new IllegalArgumentException("到达现场时间不能为空");
            }

            // 计算持续时间（单位：分钟）
            long durationMinutes = Duration.between(byId.getQueryTime(), LocalDateTime.now()).toMinutes();

            // 更新状态 + 持续时间（假设你的 updateToRepaired 支持传入 duration）
            bo = faultReportMapper.updateToRepaired(faultUpDto.getFaultId(), faultUpDto.getStatus(), durationMinutes, faultUpDto.getRepairNotes()) > 0;
        }

        // 2. 保存图片
        List<String> images = faultUpDto.getProcessImageUrls();
        if (images != null && !images.isEmpty()) {
            for (String image : images) {
                FaultImage faultImage = new FaultImage();
                faultImage.setFaultId(faultUpDto.getFaultId());
                faultImage.setImageUrl(image);
                faultImage.setImageType("progress");
                faultImageMapper.insert(faultImage);
            }
        }

        List<String> list = faultUpDto.getResultImageUrls();
        if (list != null && !list.isEmpty()) {
            for (String image : list) {
                FaultImage faultImage = new FaultImage();
                faultImage.setFaultId(faultUpDto.getFaultId());
                faultImage.setImageUrl(image);
                faultImage.setImageType(faultUpDto.getStatus());
                faultImageMapper.insert(faultImage);
            }
        }

        return bo;
    }

}