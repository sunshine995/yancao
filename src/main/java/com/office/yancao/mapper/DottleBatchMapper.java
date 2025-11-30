package com.office.yancao.mapper;

import com.office.yancao.dto.BatchDottleQueryDTO;
import com.office.yancao.entity.DottleBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface DottleBatchMapper {

    List<DottleBatch> selectBatchList(@Param("brandId") String brandId);

//    DottleBatch selectByBatchId(@Param("batchId") String batchId);
//
//    List<DottleBatch> selectByBrandId(@Param("brandId") Long brandId);

    List<DottleBatch> queryExpireBatch();

    int updateBatchRemaining(@Param("id") Long id,
                             @Param("remainingBags") Integer remainingBags,
                             @Param("remainingWeight") BigDecimal remainingWeight);

    void insert(DottleBatch batch);

    List<DottleBatch> queryAdminBatch(Map<String, Object> params);

    int updateStatusAndDaysRemaining();

    // 查询过期并且没有退回天水的烟丝
    //List<DottleBatch> queryExpireBatch();

    // int updateBatchStatus(@Param("id") Long id, @Param("status") Integer status);
}
