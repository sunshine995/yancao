package com.office.yancao.mapper.admin;

import com.office.yancao.dto.admin.Assessment.AssessmentStatsDTO;
import com.office.yancao.dto.admin.Assessment.ShiftAnalysisDTO;
import com.office.yancao.dto.admin.Assessment.TypeDistributionDTO;
import com.office.yancao.entity.admin.AssessmentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AssessmentRecordMapper {

    int insert(AssessmentRecord record);

    int updateById(AssessmentRecord record);

    AssessmentRecord selectById(Long id);

    List<AssessmentRecord> selectRecentList(int limit);

    List<AssessmentRecord> selectByType(String assessmentType);

    // 1. 获取考核统计数据
    AssessmentStatsDTO selectAssessmentStats(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // 2. 获取考核类型分布
    List<TypeDistributionDTO> selectTypeDistribution(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    // 3. 获取班次分析数据
    List<ShiftAnalysisDTO> selectShiftAnalysis(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
