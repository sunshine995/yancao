package com.office.yancao.mapper.admin;

import com.office.yancao.dto.admin.AssessmentRuleQueryDTO;
import com.office.yancao.entity.admin.AssessmentRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssessmentRuleMapper {

    List<AssessmentRule> selectList(@Param("q") AssessmentRuleQueryDTO query);

    AssessmentRule selectByRuleCode(@Param("ruleCode") String ruleCode);

    int insert(AssessmentRule rule);

    int updateById(AssessmentRule rule);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    List<AssessmentRule> selectByRuleType(String ruleType);

    void deleteByRuleType(String ruleType);

    int insertBatch(List<AssessmentRule> assessmentRules);

    List<AssessmentRule> matchRule(
            @Param("ruleType") String ruleType,
            @Param("keywords") List<String> keywords);
}

