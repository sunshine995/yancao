package com.office.yancao.controller.admin;

import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.AssessmentRuleQueryDTO;
import com.office.yancao.entity.admin.AssessmentRule;
import com.office.yancao.service.admin.AssessmentRuleService;
import com.office.yancao.untils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/assessment/rule")
@RequiredArgsConstructor
public class AssessmentRuleController {

    private final AssessmentRuleService service;

    /**
     * 工艺 / 现场 / 设备规则列表
     */
    @GetMapping("/list")
    public Result<PageInfo<AssessmentRule>> list(AssessmentRuleQueryDTO query) {
        PageInfo<AssessmentRule> list = service.list(query);
        return Result.success(list);
    }

    /**
     * 新增 / 编辑规则
     */
    @PostMapping("/save")
    public void save(@RequestBody AssessmentRule dto) {
        service.saveOrUpdate(dto);
    }

    /**
     * 启用 / 停用
     */
    @PostMapping("/status")
    public void updateStatus(@RequestParam Long id,
                             @RequestParam Integer status) {
        service.updateStatus(id, status);
    }

    @GetMapping("/export")
    public void exportExcel(
            @RequestParam String ruleType,
            HttpServletResponse response) throws Exception {

        service.exportExcel(ruleType, response);
    }

    @PostMapping("/import")
    public Result<?> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ruleType") String ruleType) throws Exception {


       try {
           int i = service.importExcel(file, ruleType);
           return Result.success(i);
       }catch (Exception e){
           return Result.fail("导入失败");
       }
    }

    /**
     * 根据违纪描述匹配考核规则
     */
    @GetMapping("/match")
    public Result<List<AssessmentRule>> matchRule(
            @RequestParam("ruleType") String ruleType,
            @RequestParam("keyword") String keyword) {

        if (keyword == null || keyword.trim().length() < 2) {
            return Result.success(Collections.emptyList());
        }

        return Result.success(service.matchRule(ruleType, keyword));
    }

}

