package com.office.yancao.mapper.admin;

import com.office.yancao.dto.admin.LearningResourceQueryDTO;
import com.office.yancao.dto.admin.ResourceListByTypeDTO;
import com.office.yancao.entity.admin.LearningResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LearningResourceMapper {

    int insert(LearningResource resource);

    LearningResource selectById(Long id);

    List<LearningResource> selectPage(LearningResourceQueryDTO learningResourceQueryDTO);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status);

    int deleteById(@Param("id") Long id);

    List<ResourceListByTypeDTO> getResourceList(Long categoryId);
}

