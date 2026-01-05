package com.office.yancao.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.SixTaskInstanceDTO;
import com.office.yancao.dto.admin.SixsReqDTO;
import com.office.yancao.dto.admin.SixsTaskDetailRespDTO;
import com.office.yancao.entity.admin.SixsTaskInstance;
import com.office.yancao.entity.admin.SixsTaskTemplate;
import com.office.yancao.service.admin.SixsTaskTemplateService;
import com.office.yancao.service.admin.TaskGenerationService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sixs/templates")
public class SixsTaskTemplateController {

    @Autowired
    private SixsTaskTemplateService taskTemplateService;

    @Autowired
    private TaskGenerationService taskGenerationService;

    @PostMapping("/save")
    public Result<SixsTaskTemplate> createTemplate(@RequestBody SixsTaskTemplate template) {
        int saved = taskTemplateService.save(template);
        return saved > 0 ? Result.success(template) : Result.fail("保存失败");
    }

    @GetMapping("/getAll")
    public Result<PageInfo<SixsTaskTemplate>> getAllTemplates(SixsReqDTO sixsReqDTO) {
        try {
            PageInfo<SixsTaskTemplate> templates = taskTemplateService.findAllActive(sixsReqDTO);
            return Result.success(templates);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

    @PutMapping("/update")
    public Result<Void> updateTemplate(@RequestBody SixsTaskTemplate template) {
        try {
            int result = taskTemplateService.update(template);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.fail("查询失败");
            }
        } catch (Exception e) {
            return Result.fail("服务器内部出现错误");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteTemplate(@PathVariable Integer id) {
        try {
            int result = taskTemplateService.deleteById(id);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            return Result.fail("删除失败");
        }
    }

    // 员工每日查询6s界面
    @GetMapping("/getSixDay")
    public Result<SixsTaskDetailRespDTO> getSixDay(@RequestParam("userId") Long userId) {
        try {
            SixsTaskDetailRespDTO sixsTaskDetailRespDTO = taskTemplateService.getSixDay(userId);
            return Result.success(sixsTaskDetailRespDTO);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

    /**
     * 提交指定任务实例
     */
    @PostMapping("/submitTasks")
    public Result<Void> submitTask(@RequestBody SixsTaskInstance sixsTaskInstance) {
        taskGenerationService.submitTask(sixsTaskInstance);
        return Result.success();
    }

    // 获取抽检列表
    @GetMapping("/getAllSixDay")
    public Result<List<SixTaskInstanceDTO>> getAllSixDay() {
        try {
            List<SixTaskInstanceDTO> allSixsDay = taskTemplateService.getAllSixsDay();
            return Result.success(allSixsDay);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

    // 根据6s任务id获取任务
    @GetMapping("/getSixTaskById")
    public Result<SixTaskInstanceDTO> getSixTaskById(@RequestParam("taskId") Long taskId) {
        try {
            SixTaskInstanceDTO sixTaskById = taskTemplateService.getSixTaskById(taskId);
            return Result.success(sixTaskById);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

    /**
     * 提交抽检任务实例
     */
    @PostMapping("/submitSpotCheck")
    public Result<Void> submitSpotCheck(@RequestBody SixsTaskInstance sixsTaskInstance) {
        taskGenerationService.submitSpotCheck(sixsTaskInstance);
        return Result.success();
    }


    /**
     * 导出Excel
     */
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        taskTemplateService.exportToExcel(response);
    }

    /**
     * 导入Excel
     */
    @PostMapping("/import")
    public Result<Map<String, Object>> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean clearAll) {

        if (file.isEmpty()) {
            return Result.fail("请选择要导入的文件");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.toLowerCase().endsWith(".xlsx") &&
                        !originalFilename.toLowerCase().endsWith(".xls"))) {
            return Result.fail("请上传Excel文件 (.xlsx 或 .xls 格式)");
        }

        try {
            SixsTaskTemplateService.ImportResult importResult =
                    taskTemplateService.importFromExcel(file, clearAll);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("successCount", importResult.getSuccessCount());
            resultData.put("failCount", importResult.getFailCount());
            resultData.put("errorMessages", importResult.getErrorMessages());

            String message = String.format("导入完成，成功：%d条，失败：%d条",
                    importResult.getSuccessCount(), importResult.getFailCount());

            if (importResult.getFailCount() > 0) {
                message += "，请查看错误信息";
            }

            return Result.success(resultData);

        } catch (Exception e) {
            return Result.fail("导入失败: " + e.getMessage());
        }
    }

}
