package com.office.yancao.controller;

import com.office.yancao.entity.VerificationData;
import com.office.yancao.service.VerificationDataService;
import com.office.yancao.untils.Result;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
public class VerificationDataController {

    @Autowired
    private VerificationDataService verificationDataService;

    @PostMapping("/save")
    public Result<String> create(@RequestBody VerificationData data) {
        verificationDataService.save(data);
        return Result.success("保存成功");
    }


    // 根据批次号和段查询一条最新记录
    @GetMapping("/byBatchIdAndSegment")
    public Result<VerificationData> getByBatchIdAndSegment(
            @RequestParam String batchId,
            @RequestParam String segment) {
        return Result.success(verificationDataService.getLatestByBatchIdAndSegment(batchId, segment));
    }

}
