package com.office.yancao.service;


import com.office.yancao.dto.RecordSubmitReqDTO;
import com.office.yancao.dto.UsageItem;
import com.office.yancao.entity.DailyShiftRecord;
import com.office.yancao.entity.UsageDetail;
import com.office.yancao.mapper.DailyShiftRecordMapper;
import com.office.yancao.mapper.UsageDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ShiftRecordService {

    @Autowired
    private DailyShiftRecordMapper recordMapper;

    @Autowired
    private UsageDetailMapper usageMapper;


    @Transactional
    public Boolean submitRecord(RecordSubmitReqDTO dto) {


        LocalDate date = dto.getDate();
        String classes = dto.getClasses();

        LocalDateTime currentStart = date.atStartOfDay();
        // 检查是否已存在
        if (recordMapper.selectTargetDate(currentStart) != null) {
            throw new RuntimeException("该日期和班组的数据已存在，请勿重复提交！");
        }

        // 获取昨日结余
        DailyShiftRecord yesterdayRecord = recordMapper.selectLatestBefore(currentStart);
        int yBox = (yesterdayRecord != null) ? yesterdayRecord.getSurplusBox() : 0;
        int yBoard = (yesterdayRecord != null) ? yesterdayRecord.getSurplusBoard() : 0;
        System.out.println(yBox);
        // 计算今日生产总量
        int produceBox = dto.getProductionBox().stream().mapToInt(u -> u.getQuantity() == null ? 0 : u.getQuantity()).sum();
        System.out.println(produceBox);
        // 计算今日使用总量
        int usedBox = dto.getBoxUsage().stream().mapToInt(u -> u.getQuantity() == null ? 0 : u.getQuantity()).sum();
        int usedBoard = dto.getBoardUsage().stream().mapToInt(u -> u.getQuantity() == null ? 0 : u.getQuantity()).sum();

        // 计算结余
        int surplusBox = yBox + produceBox - usedBox - (dto.getProductionBoard() == null ? 0 : dto.getProductionBoard());
        int surplusBoard = yBoard + (dto.getProductionBoard() == null ? 0 : dto.getProductionBoard()) - usedBoard;

        // 获取前端结余
        Integer frontendBox = dto.getSurplusBox() != null ? dto.getSurplusBox() : null;
        Integer frontendBoard = dto.getSurplusBoard() != null ? dto.getSurplusBoard() : null;
        // 3. 判断是否一致
        boolean boxMatch = Objects.equals(frontendBox, surplusBox);
        boolean boardMatch = Objects.equals(frontendBoard, surplusBoard);
        boolean isConsistent = boxMatch && boardMatch;


        // 保存主记录
        DailyShiftRecord record = new DailyShiftRecord();
        record.setRecordDate(date);
        record.setClasses(classes);
        record.setProductionBox(produceBox);
        record.setProductionBoard(dto.getProductionBoard());
        record.setSurplusBox(surplusBox);
        record.setSurplusBoard(surplusBoard);
        record.setYesterdaySurplusBox(yBox);
        record.setYesterdaySurplusBoard(yBoard);
        record.setClasses(classes);
        record.setCreatedId(dto.getCreatedId());

        int insert = recordMapper.insert(record);

        // 保存使用明细
        List<UsageDetail> details = new ArrayList<>();

        for (UsageItem item : dto.getBoxUsage()) {
            if (item.getQuantity() == null || item.getQuantity() == 0){
                continue;
            }
            UsageDetail d = new UsageDetail();
            d.setRecordId(record.getId());
            d.setMaterialType("useBox");
            d.setUsageType(item.getType());
            d.setQuantity(item.getQuantity() == null ? 0 : item.getQuantity());
            d.setRemark(item.getRemark());
            details.add(d);
        }

        for (UsageItem item : dto.getBoardUsage()) {
            if (item.getQuantity() == null || item.getQuantity() == 0){
                continue;
            }
            UsageDetail d = new UsageDetail();
            d.setRecordId(record.getId());
            d.setMaterialType("board");
            d.setUsageType(item.getType());
            d.setQuantity(item.getQuantity());
            d.setRemark(item.getRemark());
            details.add(d);
        }

        for (UsageItem item : dto.getProductionBox()) {
            if (item.getQuantity() == null || item.getQuantity() == 0){
                continue;
            }
            UsageDetail d = new UsageDetail();
            d.setRecordId(record.getId());
            d.setMaterialType("produceBox");
            d.setUsageType(item.getType());
            d.setQuantity(item.getQuantity() == null ? 0 : item.getQuantity());
            d.setRemark(item.getRemark());
            details.add(d);
        }
        usageMapper.batchInsert(details);
        return insert > 0;
    }


    public DailyShiftRecord selectByDateAndShift(){

        LocalDate date = LocalDate.now();
        LocalDateTime currentStart = date.atStartOfDay();
        DailyShiftRecord dailyShiftRecord = recordMapper.selectLatestBefore(currentStart);
        return dailyShiftRecord;
    }
}
