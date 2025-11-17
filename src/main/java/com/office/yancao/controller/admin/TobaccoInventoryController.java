package com.office.yancao.controller.admin;

import com.office.yancao.entity.admin.TobaccoInventory;
import com.office.yancao.service.admin.TobaccoInventoryService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tobacco-inventory")
public class TobaccoInventoryController {

    @Autowired
    private TobaccoInventoryService tobaccoInventoryService;

    @GetMapping("/recent-twenty-days")
    public Result<Map<String, Object>> getRecentTwentyDaysData() {
        try {
            Map<String, Object> data = tobaccoInventoryService.getRecentTwentyDaysData();
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

    @PostMapping("/save")
    public Result<String> createShellRecord(@RequestBody TobaccoInventory record){
        int one = tobaccoInventoryService.saveShellRecord(record);
        if (one > 0) {
            return Result.success("保存成功");
        }else {
            return Result.fail("保存失败");
        }

    }

    @GetMapping("/getRecent")
    public Result<TobaccoInventory> selectByInventory() {
        try {
            TobaccoInventory tobaccoInventory  = tobaccoInventoryService.selectByInventory();
            return Result.success(tobaccoInventory);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

}
