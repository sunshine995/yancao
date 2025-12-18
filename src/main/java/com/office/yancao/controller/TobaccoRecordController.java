// com.office.yancao.controller.TobaccoRecordController.java
package com.office.yancao.controller;

import com.office.yancao.dto.TobaccoRecordDTO;
import com.office.yancao.entity.TobaccoRecord;
import com.office.yancao.service.TobaccoRecordService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tobaccos")
public class TobaccoRecordController {

    private final TobaccoRecordService tobaccoRecordService;

    @Autowired
    public TobaccoRecordController(TobaccoRecordService tobaccoRecordService) {
        this.tobaccoRecordService = tobaccoRecordService;
    }

    /**
     * 获取所有工单的退出烟叶（支持搜索）
     * URL：GET /api/tobaccos?status=exit&searchKeyword={keyword}
     */
    @GetMapping("")
    public ResponseEntity<Result<List<TobaccoRecordDTO>>> getAllExitTobaccos(
            @RequestParam String status, // 固定为exit
            @RequestParam(required = false) String searchKeyword) {
        
        // 校验状态必须为exit
        if (!"exit".equals(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.fail(400, "状态参数必须为exit"));
        }
        
        // 调用服务层查询所有工单的退出烟叶
        List<TobaccoRecordDTO> result = tobaccoRecordService.getAllExitTobaccos(searchKeyword);
        return ResponseEntity.ok(Result.success(result));
    }

    /**
     * 根据工单ID查询已加入的烟叶
     */
    @GetMapping("/added/{workOrderId}")
    public ResponseEntity<Result<List<TobaccoRecordDTO>>> getAddedTobaccosByWorkOrderId(
            @PathVariable Long workOrderId) {
        
        try {
            // 调用服务层根据工单ID查询已加入的烟叶
            List<TobaccoRecordDTO> result = tobaccoRecordService.getAddedTobaccosByWorkOrderId(workOrderId);
            return ResponseEntity.ok(Result.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail(500, "查询已加入烟叶失败: " + e.getMessage()));
        }
    }

    /**
     * 添加退出烟叶记录
     * URL：POST /api/tobaccos/addExit
     */
    @PostMapping("/addExit")
    public ResponseEntity<Result<TobaccoRecordDTO>> addExitTobacco(@RequestBody TobaccoRecordDTO tobaccoRecordDTO) {
        try {
            // 转换DTO为实体
            com.office.yancao.entity.TobaccoRecord tobaccoRecord = new com.office.yancao.entity.TobaccoRecord();
            tobaccoRecord.setBrand(tobaccoRecordDTO.getBrand());
            tobaccoRecord.setBatchNumber(tobaccoRecordDTO.getBatchNumber());
            tobaccoRecord.setWeight(tobaccoRecordDTO.getWeight());
            tobaccoRecord.setExitWorkOrderId(tobaccoRecordDTO.getExitWorkOrderId());

            // 调用服务层添加退出烟叶记录
            com.office.yancao.entity.TobaccoRecord savedEntity = tobaccoRecordService.addExitTobacco(tobaccoRecord);
            // 使用Service层的convertToDTO方法进行实体到DTO的转换
            TobaccoRecordDTO result = tobaccoRecordService.convertToDTO(savedEntity);
            return ResponseEntity.ok(Result.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail(500, "添加退出烟叶失败: " + e.getMessage()));
        }
    }

    // 移除已加入的烟叶
    @PutMapping("/remove")
    public Result<TobaccoRecordDTO> removeAddedTobacco(@RequestBody Map<String, Object> params) {
        // 解析请求参数
        String brand = (String) params.get("brand");
        String batchNumber = (String) params.get("batchNumber");
        Long addWorkOrderId = Long.valueOf(params.get("addWorkOrderId").toString());

        // 调用服务层
        com.office.yancao.entity.TobaccoRecord removedEntity = tobaccoRecordService.removeAddedTobacco(brand, batchNumber, addWorkOrderId);
        TobaccoRecordDTO result = tobaccoRecordService.convertToDTO(removedEntity);
        return Result.success(result);
    }

    // 处理"加入本批料"请求
    @PutMapping("/add")
    public Result<TobaccoRecord> addToBatch(@RequestBody Map<String, Object> params) {
        try {
            String brand = (String) params.get("brand");
            String batchNumber = (String) params.get("batchNumber");
            Long addWorkOrderId = Long.valueOf(params.get("addWorkOrderId").toString());

            TobaccoRecord result = tobaccoRecordService.addToBatch(brand, batchNumber, addWorkOrderId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail(500, "加入批次失败: " + e.getMessage());
        }
    }
}