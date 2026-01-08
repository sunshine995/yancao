package com.office.yancao.service.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.LearningResourceQueryDTO;
import com.office.yancao.dto.admin.ResourceListByTypeDTO;
import com.office.yancao.entity.admin.LearningResource;
import com.office.yancao.mapper.admin.LearningResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningResourceService {

    @Autowired
    private LearningResourceMapper resourceMapper;


    public void save(LearningResource resource) {
        // 1. 基本校验
        if (resource.getTitle() == null || resource.getTitle().isEmpty()) {
            throw new RuntimeException("学习标题不能为空");
        }

        // 2. 默认值处理
        if (resource.getStatus() == null) {
            resource.setStatus(1); // 默认上架
        }
        if (resource.getDuration() == null) {
            resource.setDuration(0);
        }

        // 3. 插入
        resourceMapper.insert(resource);
    }

    public void update(LearningResource resource) {
        // 这里你可以后续补 update SQL
        resourceMapper.insert(resource);
    }

    public LearningResource getById(Long id) {
        return resourceMapper.selectById(id);
    }

    public PageInfo<LearningResource> page(LearningResourceQueryDTO learningResourceQueryDTO) {
        // ⭐ 核心：分页拦截
        PageHelper.startPage(learningResourceQueryDTO.getPageNum(), learningResourceQueryDTO.getPageSize());
        List<LearningResource> learningResources = resourceMapper.selectPage(learningResourceQueryDTO);
        return new PageInfo<>(learningResources);
    }

    public void updateStatus(Long id, Integer status) {
        resourceMapper.updateStatus(id, status);
    }

    public void deleteResource(Long id){
        resourceMapper.deleteById(id);
    }

    public List<ResourceListByTypeDTO> getResourceList(Long id) {
        List<ResourceListByTypeDTO> resourceList = resourceMapper.getResourceList(id);
        return resourceList;
    }
}
