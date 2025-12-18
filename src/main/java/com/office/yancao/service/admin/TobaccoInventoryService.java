package com.office.yancao.service.admin;

import com.office.yancao.entity.admin.TobaccoInventory;
import com.office.yancao.mapper.admin.TobaccoInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TobaccoInventoryService {

    @Autowired
    private TobaccoInventoryMapper tobaccoInventoryMapper;


    public Map<String, Object> getRecentTwentyDaysData() {
        List<TobaccoInventory> tobaccoInventories = tobaccoInventoryMapper.selectRecentTwentyDaysData();
        TobaccoInventory tobaccoInventory = tobaccoInventoryMapper.selectDailySummaryRecentTwentyDays();
        Map<String, Object> map = new HashMap<>();
        map.put("list", tobaccoInventories);
        map.put("total", tobaccoInventory);


        return map;
    }

    public int saveShellRecord(TobaccoInventory record) {
        int insert = tobaccoInventoryMapper.insert(record);
        return insert;
    }

    public TobaccoInventory selectByInventory() {
        TobaccoInventory tobaccoInventory = tobaccoInventoryMapper.selectByInventory();
        return tobaccoInventory;
    }
}
