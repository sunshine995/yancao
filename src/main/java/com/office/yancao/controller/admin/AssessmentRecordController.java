package com.office.yancao.controller.admin;

import com.office.yancao.dto.admin.Assessment.AssessmentStatsDTO;
import com.office.yancao.dto.admin.Assessment.ShiftAnalysisDTO;
import com.office.yancao.dto.admin.Assessment.TypeDistributionDTO;
import com.office.yancao.entity.admin.AssessmentRecord;
import com.office.yancao.service.admin.AssessmentRecordService;
import com.office.yancao.untils.ExportWordUtil;
import com.office.yancao.untils.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentRecordController {

    @Resource
    private AssessmentRecordService assessmentRecordService;

    /**
     * 新增考核记录
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody AssessmentRecord record) {
        try {
            assessmentRecordService.save(record);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("保存失败");
        }
    }

    /**
     * 修改考核记录
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody AssessmentRecord record) {
        try {
            assessmentRecordService.update(record);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新失败");
        }
    }

    /**
     * 根据ID查询详情
     */
    @GetMapping("/{id}")
    public Result<AssessmentRecord> detail(@PathVariable Long id) {
        try {
            AssessmentRecord record = assessmentRecordService.getById(id);
            return Result.success(record);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
    }

    /**
     * 查询最近考核记录
     */
    @GetMapping("/recent")
    public Result<List<AssessmentRecord>> recent(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            return Result.success(assessmentRecordService.getRecentList(limit));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
    }

    /**
     * 按考核类型查询
     */
    @GetMapping("/type/{type}")
    public Result<List<AssessmentRecord>> listByType(@PathVariable String type) {
        try {
            return Result.success(assessmentRecordService.getByType(type));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询失败");
        }
    }


    @PostMapping("/export/process")
    public void exportProcessWord(
            @RequestBody AssessmentRecord record,
            HttpServletResponse response) throws Exception {

        // 3. 模板数据
        Map<String, Object> data = new HashMap<>();
        data.put("position", record.getPosition());
        data.put("shift", record.getShift());
        data.put("person", record.getPerson());
        data.put("assessmentDate", record.getAssessmentDate());
        data.put("content", record.getContent());
        data.put("opinion", record.getOpinion());
        data.put("remark", record.getRemark());


        ExportWordUtil.exportWordDocument("templates/process.docx", data, response, "export.docx");

    }

    @PostMapping("/export/site")
    public void exportSiteWord(
            @RequestBody AssessmentRecord record,
            HttpServletResponse response) throws Exception {

        // 3. 模板数据
        Map<String, Object> data = new HashMap<>();
        data.put("position", record.getPosition());
        data.put("shift", record.getShift());
        data.put("person", record.getPerson());
        data.put("assessmentDate", record.getAssessmentDate());
        data.put("content", record.getContent());
        data.put("opinion", record.getOpinion());
        data.put("remark", record.getRemark());


        ExportWordUtil.exportWordDocument("templates/site123.docx", data, response, "export.docx");

    }


    // 1. 获取考核统计数据
    @GetMapping("/dashboard/stats")
    public Result<?> getAssessmentStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // 1. 处理默认日期范围（核心：补全null的参数）
        LocalDate now = LocalDate.now();
        if (startDate == null) {
            // 不传开始日期，默认近7天
            startDate = now.minusDays(30);
        }
        if (endDate == null) {
            // 不传结束日期，默认今天
            endDate = now;
        }
        // 额外兜底：防止传了startDate但没传endDate（或反过来）导致范围异常
        if (startDate.isAfter(endDate)) {
            return Result.fail("开始日期不能晚于结束日期");
        }

        AssessmentStatsDTO stats = assessmentRecordService.getAssessmentStats(startDate, endDate);

        return Result.success(stats);
    }

    // 2. 获取考核类型分布
    @GetMapping("/charts/type-distribution")
    public Result<?> getTypeDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // 1. 处理默认日期范围（核心：补全null的参数）
        LocalDate now = LocalDate.now();
        if (startDate == null) {
            // 不传开始日期，默认近7天
            startDate = now.minusDays(30);
        }
        if (endDate == null) {
            // 不传结束日期，默认今天
            endDate = now;
        }
        // 额外兜底：防止传了startDate但没传endDate（或反过来）导致范围异常
        if (startDate.isAfter(endDate)) {
            return Result.fail("开始日期不能晚于结束日期");
        }

        List<TypeDistributionDTO> data = assessmentRecordService.getTypeDistribution(startDate, endDate);

        return Result.success(data);
    }

    // 3. 获取班次分析数据
    @GetMapping("/charts/shift-analysis")
    public Result<?> getShiftAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // 1. 处理默认日期范围（核心：补全null的参数）
        LocalDate now = LocalDate.now();
        if (startDate == null) {
            // 不传开始日期，默认近7天
            startDate = now.minusDays(30);
        }
        if (endDate == null) {
            // 不传结束日期，默认今天
            endDate = now;
        }
        // 额外兜底：防止传了startDate但没传endDate（或反过来）导致范围异常
        if (startDate.isAfter(endDate)) {
            return Result.fail("开始日期不能晚于结束日期");
        }

        List<ShiftAnalysisDTO> data = assessmentRecordService.getShiftAnalysis(startDate, endDate);

        return Result.success(data);
    }



}
