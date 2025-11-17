package com.office.yancao.mapper.admin;

import com.office.yancao.entity.admin.TobaccoInventory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TobaccoInventoryMapper {

    // 插入一条数据
    int insert(TobaccoInventory record);

    List<TobaccoInventory> selectRecentTwentyDaysData();

    TobaccoInventory selectDailySummaryRecentTwentyDays();

    TobaccoInventory selectByInventory();
}
