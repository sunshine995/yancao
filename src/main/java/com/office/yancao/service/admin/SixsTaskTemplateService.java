package com.office.yancao.service.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.SixTaskInstanceDTO;
import com.office.yancao.dto.admin.SixsReqDTO;
import com.office.yancao.dto.admin.SixsTaskDetailRespDTO;
import com.office.yancao.entity.admin.SixsTaskInstance;
import com.office.yancao.entity.admin.SixsTaskTemplate;
import com.office.yancao.mapper.admin.SixsTaskInstanceMapper;
import com.office.yancao.mapper.admin.SixsTaskTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SixsTaskTemplateService {

    @Autowired
    private SixsTaskTemplateMapper taskTemplateMapper;

    @Autowired
    private SixsTaskInstanceMapper sixsTaskInstanceMapper;

    public int save(SixsTaskTemplate template) {
        // 设置默认值
        if (template.getRequiredImages() == null) {
            template.setRequiredImages(1);
        }
        if (template.getIsActive() == null) {
            template.setIsActive(true);
        }
        return taskTemplateMapper.insert(template);
    }

    public PageInfo<SixsTaskTemplate> findAllActive(SixsReqDTO sixsReqDTO) {
        PageHelper.startPage(sixsReqDTO.getPageNum(), sixsReqDTO.getPageSize());
        //System.out.println(sixsReqDTO.getGroupName());
        List<SixsTaskTemplate> byCondition = taskTemplateMapper.findByCondition(sixsReqDTO);
        return new PageInfo<>(byCondition);
    }

    public int update(SixsTaskTemplate template) {
        return taskTemplateMapper.update(template);
    }

    public int deleteById(Integer id) {
        return taskTemplateMapper.deleteById(id);
    }


    // 根据员工Id获取每日6s
    public SixsTaskDetailRespDTO getSixDay(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }

        LocalDate today = LocalDate.now(); // 仅日期，不带时间

        // 获取当前时间
        SixsTaskInstance byEmployeeAndDate = sixsTaskInstanceMapper.findByEmployeeAndDate(userId, today);
        if (byEmployeeAndDate == null){
            return new SixsTaskDetailRespDTO();
        }

        Integer templateId = byEmployeeAndDate.getTemplateId();
        if (templateId == null) {
            return new SixsTaskDetailRespDTO();
        }

        SixsTaskTemplate sixsTaskTemplate = taskTemplateMapper.selectTemplateById(byEmployeeAndDate.getTemplateId());
        if (sixsTaskTemplate == null) {
            return new SixsTaskDetailRespDTO();
        }
        SixsTaskDetailRespDTO sixsTaskDetailRespDTO = new SixsTaskDetailRespDTO();

        sixsTaskDetailRespDTO.setTaskInfo(sixsTaskTemplate);
        sixsTaskDetailRespDTO.setTaskInstance(byEmployeeAndDate);

        return sixsTaskDetailRespDTO;
    }


    // 获取每日6s列表
    public List<SixTaskInstanceDTO> getAllSixsDay(){
        return sixsTaskInstanceMapper.selectSpotCheckTasks();
    }

    public SixTaskInstanceDTO getSixTaskById(Long taskId) {
        return sixsTaskInstanceMapper.getSixTaskById(taskId);
    }

    // 导出文件
    public void exportToExcel(HttpServletResponse response) {
        try {
            // 设置响应头
            String fileName = URLEncoder.encode("6S任务模板", String.valueOf(StandardCharsets.UTF_8)).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 查询数据
             List<SixsTaskTemplate> templates = taskTemplateMapper.findAll();

            // 导出Excel
            EasyExcel.write(response.getOutputStream(), SixsTaskTemplate.class)
                    .sheet("6S任务模板")
                    .doWrite(templates);
        } catch (IOException e) {
//            log.error("导出Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Transactional
    public ImportResult importFromExcel(MultipartFile file, boolean clearAll) {
        ImportResult result = new ImportResult();
        List<SixsTaskTemplate> templates = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try {
            // 如果选择清空原有数据
            if (clearAll) {
                taskTemplateMapper.truncateTable();
                //log.info("已清空原有模板数据");
            }

            // 读取Excel文件
            EasyExcel.read(file.getInputStream(), SixsTaskTemplate.class, new ReadListener<SixsTaskTemplate>() {


                public void invoke(SixsTaskTemplate data, AnalysisContext context) {
                    // 数据校验
                    String error = validateTemplate(data);
                    if (error != null) {
                        int rowIndex = context.readRowHolder().getRowIndex() + 1;
                        errorMessages.add("第" + rowIndex + "行: " + error);
                        result.setFailCount(result.getFailCount() + 1);
                    } else {
                        // 设置默认值
                        if (data.getIsActive() == null) {
                            data.setIsActive(true);
                        }
                        if (data.getRequiredImages() == null) {
                            data.setRequiredImages(1);
                        }
                        templates.add(data);
                    }
                }


                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 所有数据解析完成后批量处理
                    if (!templates.isEmpty()) {
                        int success = taskTemplateMapper.batchInsertOrUpdate(templates);
                        result.setSuccessCount(success);
                    }
                }
            }).sheet().doRead();

            result.setErrorMessages(errorMessages);
            return result;

        } catch (Exception e) {
            //log.error("导入Excel失败", e);
            throw new RuntimeException("导入失败: " + e.getMessage());
        }
    }

    /**
     * 验证模板数据
     */
    private String validateTemplate(SixsTaskTemplate template) {
        if (template.getTemplateName() == null || template.getTemplateName().trim().isEmpty()) {
            return "模板名称不能为空";
        }
        if (template.getGroupName() == null || template.getGroupName().trim().isEmpty()) {
            return "所属分组不能为空";
        }
        if (template.getShift() == null || template.getShift().trim().isEmpty()) {
            return "班次不能为空";
        }
        if (template.getWeekday() == null || template.getWeekday() < 1 || template.getWeekday() > 7) {
            return "星期几必须为1-7之间的数字";
        }
        if (template.getTaskContent() == null || template.getTaskContent().trim().isEmpty()) {
            return "任务内容不能为空";
        }
        return null;
    }

    public static class ImportResult {
        private int successCount;
        private int failCount;
        private List<String> errorMessages = new ArrayList<>();

        // getters and setters
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailCount() { return failCount; }
        public void setFailCount(int failCount) { this.failCount = failCount; }

        public List<String> getErrorMessages() { return errorMessages; }
        public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }

        public void addErrorMessage(String message) {
            this.errorMessages.add(message);
        }
    }
}
