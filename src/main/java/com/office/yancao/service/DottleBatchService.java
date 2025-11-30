package com.office.yancao.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.ReceiveDottleDTO;
import com.office.yancao.entity.DottleBatch;
import com.office.yancao.entity.DottleOperation;
import com.office.yancao.mapper.DottleBatchMapper;
import com.office.yancao.mapper.DottleOperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DottleBatchService {

    @Autowired
    private DottleBatchMapper batchMapper;

    @Autowired
    private DottleOperationMapper operationMapper;

    @Transactional
    public void receiveTobacco(ReceiveDottleDTO receiveDTO){
        // 计算过期日期（10天后）
        LocalDate receiveDate = LocalDate.now();
        LocalDate expireDate = receiveDate.plusDays(30);

        DottleBatch batch = new DottleBatch();

        batch.setBatchId(receiveDTO.getBatchId());
        batch.setBrandId(receiveDTO.getBrandId());
        batch.setReceiveDate(receiveDate);
        batch.setExpireDate(expireDate);
        batch.setTotalBags(receiveDTO.getTotalBags());
        batch.setTotalWeight(receiveDTO.getTotalWeight());
        batch.setRemainingBags(receiveDTO.getTotalBags());
        batch.setRemainingWeight(receiveDTO.getTotalWeight());
        batch.setStatus(receiveDTO.getStatus()); // 正常
        batch.setDaysRemaining(30);
        batch.setShiftType(receiveDTO.getShiftType());
        batch.setOperator(receiveDTO.getOperator());
        batch.setCreateTime(LocalDateTime.now());
        batch.setUpdateTime(LocalDateTime.now());

        batchMapper.insert(batch);

        // 记录操作
        DottleOperation operation = new DottleOperation();

        operation.setBatchId(batch.getId());
        operation.setOperationType(1); // 接收
        operation.setBagsUsed(receiveDTO.getTotalBags());
        operation.setWeightUsed(receiveDTO.getTotalWeight());
        operation.setShiftType(receiveDTO.getShiftType());
        operation.setOperator(receiveDTO.getOperator());
        operation.setOperationTime(LocalDateTime.now());

        operation.setRemark(receiveDTO.getRemark());
        operation.setCreateTime(LocalDateTime.now());

        operationMapper.insert(operation);

        // log.info("烟丝接收成功，批次号：{}", batchId);
    }

    public List<DottleBatch> selectDottleList(String brandId) {
        List<DottleBatch> dottleBatches = batchMapper.selectBatchList(brandId);
        return dottleBatches;
    }

    public List<DottleOperation> queryDayDottle() {
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusSeconds(1); // 23:59:59
        List<DottleOperation> dottleOperations = operationMapper.selectSummary(2, start, end);
        return dottleOperations;
    }

    public PageInfo<DottleBatch> queryAdminBatch(String brandId, String status, String sortField, String sortOrder, int page, int pageSize) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, pageSize);
        // 构建查询参数Map
        Map<String, Object> params = new HashMap<>();
        params.put("brandId", brandId);
        params.put("status", status);
        params.put("sortField", sortField);
        params.put("sortOrder", sortOrder);


        // 执行查询
        List<DottleBatch> batches = batchMapper.queryAdminBatch(params);
        return new PageInfo<>(batches);
    }

    public void updateBatchStatusDaily() {
        int updated = batchMapper.updateStatusAndDaysRemaining();
    }

    public List<DottleOperation> queryOperationBatch() {
       return operationMapper.queryRecentWeekOperations();
    }

    public List<DottleBatch> queryExpireBatch() {
        return batchMapper.queryExpireBatch();
    }
}
