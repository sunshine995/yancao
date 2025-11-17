package com.office.yancao.controller;

import com.office.yancao.dto.RecordSubmitReqDTO;
import com.office.yancao.entity.DailyShiftRecord;
import com.office.yancao.service.ShiftRecordService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
