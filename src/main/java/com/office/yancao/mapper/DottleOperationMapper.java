package com.office.yancao.mapper;

import com.office.yancao.entity.DottleOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface DottleOperationMapper {

    List<DottleOperation> selectOperationList(@Param("batchId") Long batchId,
                                              @Param("operationType") Integer operationType,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    void insert(DottleOperation operation);

    // 查询每日掺兑记录
    List<DottleOperation> selectSummary(@Param("operationType") Integer operationType,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
}
