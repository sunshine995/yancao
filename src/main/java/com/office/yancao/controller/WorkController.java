package com.office.yancao.controller;


import com.office.yancao.dto.WorkDTO;
import com.office.yancao.entity.Work;
import com.office.yancao.service.WorkService;
import com.office.yancao.untils.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work")
public class WorkController {

    @Resource
    private WorkService workService;

    /**
     * 保存工单
     */
    @PostMapping("/save")
    public Result<String> saveWork(@RequestBody WorkDTO work) {
        boolean success = workService.saveWork(work);
        return success ? Result.success("工单保存成功") : Result.fail("工单保存失败");
    }

    /**
     * 根据ID查询工单
     */
    @GetMapping("/today-work-orders")
    public Result getTodayWorkOrders(@RequestParam("id") Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            return Result.fail(400, "用户ID不能为空");
        }

        try {
            // 查询今日工单
            Map<String, Object> data = workService.getTodayWorkOrdersByUserId(id);
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有工单
     */
    @GetMapping("/listAll")
    public Result<List<Work>> listAllWorks() {
        List<Work> workList = workService.listAllWorks();
        return Result.success(workList);
    }

    /**
     * 根据用户ID查询工单
     */
    @GetMapping("/listByUserId")
    public Result<List<Work>> listWorksByUserId(@RequestParam Long userId) {
        List<Work> workList = workService.listWorksByUserId(userId);
        return Result.success(workList);
    }

}