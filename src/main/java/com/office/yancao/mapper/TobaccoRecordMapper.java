// com.office.yancao.mapper.TobaccoRecordMapper.java
package com.office.yancao.mapper;

import com.office.yancao.entity.TobaccoRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TobaccoRecordMapper {

    /**
     * 带搜索关键字查询所有工单的退出烟叶
     * @param keyword 搜索关键字（牌号/批次号，可选）
     * @return 所有工单中符合条件的退出烟叶列表
     */
    List<TobaccoRecord> selectAllExitTobaccosWithKeyword(@Param("keyword") String keyword);

    /**
     * 不带搜索关键字查询所有工单的退出烟叶
     * @return 所有工单的全部退出烟叶列表
     */
    List<TobaccoRecord> selectAllExitTobaccosWithoutKeyword();

    /**
     * 插入退出烟叶记录
     * @param record 退出烟叶记录
     * @return 影响的行数
     */
    int insertExitTobacco(TobaccoRecord record);

    

    /**
     * 根据工单ID查询已加入的烟叶
     * @param workOrderId 工单ID
     * @return 已加入该工单的烟叶列表
     */
    List<TobaccoRecord> selectAddedTobaccosByWorkOrderId(@Param("workOrderId") Long workOrderId);

    //移除按钮 更新状态为exit，清空addWorkOrderId，刷新操作时间
    int updateToExit(
        @Param("brand") String brand,
        @Param("batchNumber") String batchNumber,
        @Param("addWorkOrderId") Long addWorkOrderId,
        @Param("newOperateTime") java.time.LocalDateTime newOperateTime
    );

    // 更新为“已加入状态”，设置工单ID和操作时间
    int updateToAdded(
            @Param("brand") String brand,
            @Param("batchNumber") String batchNumber,
            @Param("addWorkOrderId") Long addWorkOrderId,
            @Param("newOperateTime") LocalDateTime newOperateTime
    );
}