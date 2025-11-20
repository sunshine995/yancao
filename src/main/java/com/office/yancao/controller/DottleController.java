package com.office.yancao.controller;


import com.office.yancao.dto.FaultReportDTO;
import com.office.yancao.dto.ReceiveDottleDTO;
import com.office.yancao.entity.DottleBatch;
import com.office.yancao.entity.DottleOperation;
import com.office.yancao.service.DottleBatchService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/dottle")
public class DottleController {

    @Autowired
    private DottleBatchService dottleBatchService;

    /**
     * 接收残烟丝
     */
    @PostMapping("/save")
    public Result<Long> saveBatch(@RequestBody ReceiveDottleDTO receiveDottleDTO) {
        System.out.println(receiveDottleDTO.getTotalBags());
        dottleBatchService.receiveTobacco(receiveDottleDTO);
        return Result.success(); // ✅ 返回成功 + 数据
    }

    /**
     * 查询残烟丝
     */
    @GetMapping("/query")
    public Result<List<DottleBatch>> queryBatch(@RequestParam("brandId") String brandId) {
        List<DottleBatch> dottleBatch = dottleBatchService.selectDottleList(brandId);
        return Result.success(dottleBatch); // ✅ 返回成功 + 数据
    }

    /**
     * 查询当天残烟丝
     */
    @GetMapping("/queryDayDottle")
    public Result<List<DottleOperation>> queryDayDottle() {
        List<DottleOperation> dottleBatch = dottleBatchService.queryDayDottle();
        return Result.success(dottleBatch); // ✅ 返回成功 + 数据
    }
}
