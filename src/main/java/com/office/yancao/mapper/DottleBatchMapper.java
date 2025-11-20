package com.office.yancao.mapper;

import com.office.yancao.dto.BatchDottleQueryDTO;
import com.office.yancao.entity.DottleBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DottleBatchMapper {

    List<DottleBatch> selectBatchList(@Param("brandId") String brandId);

//    DottleBatch selectByBatchId(@Param("batchId") String batchId);
//
//    List<DottleBatch> selectByBrandId(@Param("brandId") Long brandId);

    List<DottleBatch> selectExpiringBatches(@Param("days") Integer days);

    int updateBatchRemaining(@Param("id") Long id,
                             @Param("remainingBags") Integer remainingBags,
                             @Param("remainingWeight") BigDecimal remainingWeight);

    void insert(DottleBatch batch);

    // int updateBatchStatus(@Param("id") Long id, @Param("status") Integer status);
}
