package com.office.yancao.controller;

import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.BoxSettlementQueryDTO;
import com.office.yancao.dto.RecordSubmitReqDTO;
import com.office.yancao.entity.DailyShiftRecord;
import com.office.yancao.service.ShiftRecordService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

// 纸板使用情况
@RestController
@RequestMapping("/api/recordBox")
public class RecordBoxController {
    @Autowired
    private ShiftRecordService recordService;

    @PostMapping("/save")
    public Result<Void> submit(@RequestBody RecordSubmitReqDTO dto) {
        try {
            recordService.submitRecord(dto);
            return Result.success();
        }catch (Exception e){
            return Result.fail("保存失败");
        }

    }



    @GetMapping("/get")
    public Result<DailyShiftRecord> selectByBoxDate() {
        DailyShiftRecord dailyShiftRecord = recordService.selectByDateAndShift();
        return Result.success(dailyShiftRecord);
    }

    /**
     * 查询每日箱皮结算记录
     * @param classes 班组（甲班/乙班）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param pageSize 每页大小
     * @return 箱皮结算记录列表
     */
    @GetMapping("/settlement")
    public Result<PageInfo<DailyShiftRecord>> selectBoxSettlementRecords(
            @RequestParam(required = false) String classes,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 封装查询参数
            BoxSettlementQueryDTO queryDTO = new BoxSettlementQueryDTO();
            queryDTO.setClasses(classes);
            queryDTO.setStartDate(startDate);
            queryDTO.setEndDate(endDate);
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            
            // 调用Service层方法
            PageInfo<DailyShiftRecord> pageInfo = recordService.selectBoxSettlementRecords(queryDTO);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.fail("查询箱皮结算记录失败: " + e.getMessage());
        }
    }

}
