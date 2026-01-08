package com.office.yancao.service.admin;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.AssessmentRuleExcelDTO;
import com.office.yancao.dto.admin.AssessmentRuleQueryDTO;
import com.office.yancao.entity.admin.AssessmentRule;
import com.office.yancao.mapper.admin.AssessmentRuleMapper;
import com.office.yancao.untils.SimpleKeywordSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentRuleService {

    @Autowired
    private SimpleKeywordSplitter keywordSplitter;

    private final AssessmentRuleMapper mapper;


    public PageInfo<AssessmentRule> list(AssessmentRuleQueryDTO query) {
        // ⭐ 核心：分页拦截
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AssessmentRule> list = mapper.selectList(query);

        return new PageInfo<>(list);
    }


    public void saveOrUpdate(AssessmentRule dto) {

        if (dto.getId() == null) {
            // 新增
            AssessmentRule exists = mapper.selectByRuleCode(dto.getRuleCode());
            if (exists != null) {
                throw new RuntimeException("规则编码已存在");
            }

            AssessmentRule rule = new AssessmentRule();
            BeanUtils.copyProperties(dto, rule);
            mapper.insert(rule);
        } else {
            // 修改
            AssessmentRule rule = new AssessmentRule();
            BeanUtils.copyProperties(dto, rule);
            mapper.updateById(rule);
        }
    }


    public void updateStatus(Long id, Integer status) {
        mapper.updateStatus(id, status);
    }


    public void exportExcel(String ruleType, HttpServletResponse response) throws Exception {

        List<AssessmentRule> list =
                mapper.selectByRuleType(ruleType);

        List<AssessmentRuleExcelDTO> excelList =
                list.stream()
                        .map(r -> {
                            AssessmentRuleExcelDTO dto = new AssessmentRuleExcelDTO();
                            BeanUtils.copyProperties(r, dto);
                            return dto;
                        })
                        .collect(Collectors.toList());


        String fileName = URLEncoder.encode(
                getRuleTypeName(ruleType) + "考核规则.xlsx",
                String.valueOf(StandardCharsets.UTF_8)
        );

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader(
                "Content-disposition",
                "attachment;filename=" + fileName
        );

        EasyExcel.write(response.getOutputStream(), AssessmentRuleExcelDTO.class)
                .sheet("规则列表")
                .doWrite(excelList);
    }

    private String getRuleTypeName(String ruleType) {
        switch (ruleType) {
            case "PROCESS":
                return "工艺";
            case "SITE":
                return "生产现场";
            case "EQUIPMENT":
                return "设备";
            default:
                return "";
        }
    }


    // 主方法：控制整体流程，但不包住插入
    public int importExcel(MultipartFile file, String ruleType) throws Exception {
        List<AssessmentRuleExcelDTO> list = EasyExcel.read(file.getInputStream())
                .head(AssessmentRuleExcelDTO.class)
                .sheet()
                .doReadSync();

        if (list == null || list.isEmpty()) {
            throw new RuntimeException("Excel 中未读取到任何规则数据");
        }
        System.out.println(list.size());

        // 删除旧数据（可保留事务）
        deleteOldRules(ruleType);

        int successCount = 0;
        int index = 0;

        for (AssessmentRuleExcelDTO dto : list) {
            index++;
            try {
                AssessmentRule rule = new AssessmentRule();
                BeanUtils.copyProperties(dto, rule);
                rule.setRuleType(ruleType);

                // 调用独立事务方法
                if (insertSingleRule(rule)) {
                    successCount++;
                } else {
                    System.err.println("⚠️ 第 " + index + " 行插入失败（返回0）");
                }
            } catch (Exception e) {
                System.err.println("❌ 第 " + index + " 行异常: " + e.getMessage());
            }
        }

        return successCount;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOldRules(String ruleType) {
        mapper.deleteByRuleType(ruleType);
    }

    // ⭐ 关键：REQUIRES_NEW 确保每次插入都是独立事务
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean insertSingleRule(AssessmentRule rule) {
        int inserted = mapper.insert(rule);
        return inserted > 0;
    }

    // 匹配
    public List<AssessmentRule> matchRule(String ruleType, String keyword) {

        // 1. 智能分词
        List<String> keywords = keywordSplitter.splitKeyword(keyword);


        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }
        List<AssessmentRule> assessmentRules = mapper.matchRule(ruleType, keywords);

        // 3. 二次计算更精确的分数
        List<AssessmentRule> scoredRules = assessmentRules.stream()
                .map(rule -> {
                    rule.setMatchScore(calculateSimpleScore(rule, keywords));
                    return rule;
                })
                .filter(rule -> rule.getMatchScore() > 0)  // 过滤零分
                .sorted(Comparator.comparing(AssessmentRule::getMatchScore).reversed())
                .limit(20)
                .collect(Collectors.toList());
        return scoredRules;
    }

    /**
     * 简单计分规则
     */
    private double calculateSimpleScore(AssessmentRule rule, List<String> keywords) {
        String ruleName = rule.getRuleName().toLowerCase();
        double score = 0;

        for (String keyword : keywords) {
            if (ruleName.equals(keyword)) {
                score += 3.0;  // 完全匹配
            } else if (ruleName.contains(keyword)) {
                score += 1.0;  // 部分匹配
            }

            // 关键词越长，权重越高
            if (keyword.length() >= 3) {
                score += 0.5;
            }
        }

        // 归一化到0-100分
        double maxScore = keywords.size() * 3.5;
        return (score / maxScore) * 100;
    }

}

