package com.office.yancao.mapper;

import com.office.yancao.entity.FaultReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FaultReportMapper {
    int insert(FaultReport faultReport);

    FaultReport findById(Long id);

    int updateToArrived(@Param("id") Long id, @Param("status") String status);

    int updateToRepaired(@Param("id") Long id, @Param("status") String status,
                         @Param("durationMinutes") Long durationMinutes,
                         @Param("repairNotes") String repairNotes);

    int updateToGet(@Param("id") Long id, @Param("status") String status);

    List<FaultReport> findAll(@Param("offset") int offset, @Param("limit") int limit);

    List<FaultReport> selectByType(@Param("type") String type);

    List<FaultReport> selectByAdmin(@Param("type") String type);

    List<FaultReport> selectByUserId(@Param("userId") Long userId, @Param("type") String type);
}
