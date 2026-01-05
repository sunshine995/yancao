package com.office.yancao.service.admin;

import com.office.yancao.dto.admin.Assessment.AssessmentStatsDTO;
import com.office.yancao.dto.admin.Assessment.ShiftAnalysisDTO;
import com.office.yancao.dto.admin.Assessment.TypeDistributionDTO;
import com.office.yancao.entity.admin.AssessmentRecord;
import com.office.yancao.mapper.admin.AssessmentRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
public class AssessmentRecordService {

    @Resource
    private AssessmentRecordMapper mapper;


    public void save(AssessmentRecord record) {
        record.setRectifyStatus(0);
        mapper.insert(record);
    }

    public void update(AssessmentRecord record) {
        mapper.updateById(record);
    }

    public AssessmentRecord getById(Long id) {
        return mapper.selectById(id);
    }

    public List<AssessmentRecord> getRecentList(int limit) {
        return mapper.selectRecentList(limit);
    }

    public List<AssessmentRecord> getByType(String type) {
        return mapper.selectByType(type);
    }

    public AssessmentStatsDTO getAssessmentStats(LocalDate startDate, LocalDate endDate) {
        System.out.println(startDate);
        return mapper.selectAssessmentStats(startDate, endDate);
    }

    public List<TypeDistributionDTO> getTypeDistribution(LocalDate startDate, LocalDate endDate) {
        return mapper.selectTypeDistribution(startDate, endDate);
    }

    public List<ShiftAnalysisDTO> getShiftAnalysis(LocalDate startDate, LocalDate endDate) {
        return mapper.selectShiftAnalysis(startDate, endDate);
    }
}
