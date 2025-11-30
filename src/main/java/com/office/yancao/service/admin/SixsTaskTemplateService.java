package com.office.yancao.service.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.SixsReqDTO;
import com.office.yancao.entity.admin.SixsTaskTemplate;
import com.office.yancao.mapper.admin.SixsTaskTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SixsTaskTemplateService {

    @Autowired
    private SixsTaskTemplateMapper taskTemplateMapper;

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


}
