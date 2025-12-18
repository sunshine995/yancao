package com.office.yancao.service.admin;

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

import java.time.LocalDate;
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
}
