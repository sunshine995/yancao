// com.office.yancao.service.TobaccoRecordService.java
package com.office.yancao.service;

import com.office.yancao.dto.TobaccoRecordDTO;
import com.office.yancao.entity.TobaccoRecord;
import com.office.yancao.mapper.TobaccoRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // 直接作为服务类，无需接口
public class TobaccoRecordService {

    private final TobaccoRecordMapper tobaccoRecordMapper;

    @Autowired
    public TobaccoRecordService(TobaccoRecordMapper tobaccoRecordMapper) {
        this.tobaccoRecordMapper = tobaccoRecordMapper;
    }

    // 直接在服务类中实现方法（无需接口定义）
    public List<TobaccoRecordDTO> getAllExitTobaccos(String searchKeyword) {
        List<TobaccoRecord> records;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            records = tobaccoRecordMapper.selectAllExitTobaccosWithKeyword(searchKeyword);
        } else {
            records = tobaccoRecordMapper.selectAllExitTobaccosWithoutKeyword();
        }
        
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 根据工单ID查询已加入的烟叶
    public List<TobaccoRecordDTO> getAddedTobaccosByWorkOrderId(Long workOrderId) {
        List<TobaccoRecord> records = tobaccoRecordMapper.selectAddedTobaccosByWorkOrderId(workOrderId);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

     // 3. 添加退出烟叶记录
    public TobaccoRecord addExitTobacco(TobaccoRecord tobaccoRecord) {
       
        // 设置固定字段值（与SQL插入逻辑一致）
        tobaccoRecord.setStatus("exit"); // 状态固定为"exit"
        tobaccoRecord.setSubmitStatus("unsubmitted"); // 提交状态固定为未提交
        tobaccoRecord.setAddWorkOrderId(null); // 初始未加入任何工单
        tobaccoRecord.setOperateTime(java.time.LocalDateTime.now()); // 操作时间设为当前时间
        tobaccoRecord.setIsTimeout(false); // 默认为未超时
        // 插入数据库
        int rows = tobaccoRecordMapper.insertExitTobacco(tobaccoRecord);
        if (rows != 1) {
            throw new RuntimeException("添加退出烟叶失败，请重试");
        }
        // 插入成功后，将实体转换为DTO返回
        return tobaccoRecord;
    }

    // 4. 移除已加入的烟叶（更新为退出状态）
    public TobaccoRecord removeAddedTobacco(String brand, String batchNumber, Long addWorkOrderId) {
        // 1. 参数校验
        if (brand == null || brand.trim().isEmpty()) {
            throw new RuntimeException("牌号不能为空");
        }
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            throw new RuntimeException("批次号不能为空");
        }
        if (addWorkOrderId == null) {
            throw new RuntimeException("工单ID不能为空");
        }

        // 2. 执行更新（状态改为exit，清空add_work_order_id，刷新操作时间）
        LocalDateTime now = LocalDateTime.now();
        int rows = tobaccoRecordMapper.updateToExit(brand, batchNumber, addWorkOrderId, now);
        if (rows != 1) {
            throw new RuntimeException("移除失败，请重试");
        }
        
        // 3. 创建并返回更新后的实体对象
        TobaccoRecord record = new TobaccoRecord();
        record.setBrand(brand);
        record.setBatchNumber(batchNumber);
        record.setStatus("exit");
        record.setAddWorkOrderId(null);
        record.setOperateTime(now);
        return record;
    }

public TobaccoRecord addToBatch(String brand, String batchNumber, Long addWorkOrderId) {
        // 参数校验
        if (brand == null || brand.trim().isEmpty()) {
            throw new RuntimeException("牌号不能为空");
        }
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            throw new RuntimeException("批次号不能为空");
        }
        if (addWorkOrderId == null) {
            throw new RuntimeException("工单ID不能为空");
        }

        
        // 执行更新操作
        LocalDateTime now = LocalDateTime.now();
        int rows = tobaccoRecordMapper.updateToAdded(brand, batchNumber, addWorkOrderId, now);
        if (rows == 0) {
            throw new RuntimeException("未找到对应牌号和批次号的退出烟叶记录，或该记录状态不是'exit'，无法加入批次");
        }

        // 更新返回结果的状态和时间
        TobaccoRecord record = new TobaccoRecord();
        record.setBrand(brand);
        record.setBatchNumber(batchNumber);
        record.setStatus("added");
        record.setAddWorkOrderId(addWorkOrderId);
        record.setOperateTime(now);
        return record;
    }


    public TobaccoRecordDTO convertToDTO(TobaccoRecord record) {
        TobaccoRecordDTO dto = new TobaccoRecordDTO();
        dto.setId(record.getId());
        dto.setBrand(record.getBrand());
        dto.setBatchNumber(record.getBatchNumber());
        dto.setWeight(record.getWeight());
        // 根据记录状态设置对应的时间字段
        if ("exit".equals(record.getStatus())) {
            dto.setExitTime(record.getOperateTime());
        } else if ("added".equals(record.getStatus())) {
            dto.setAddTime(record.getOperateTime());
        }
        dto.setIsTimeout(record.getIsTimeout());
        dto.setExitWorkOrderId(record.getExitWorkOrderId());
        dto.setAddWorkOrderId(record.getAddWorkOrderId());
        return dto;
    }
}