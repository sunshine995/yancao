package com.office.yancao.mapper;

import com.office.yancao.entity.TobaccoGrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TobaccoGradeMapper {
    /**
     * 查询所有烟品信息
     */

    List<TobaccoGrade> findAll();

    /**
     * 根据ID查询烟品信息
     */

    TobaccoGrade findById(Long id);

    /**
     * 根据牌号名称查询烟品信息
     */

    List<TobaccoGrade> findByGradeName(String gradeName);

    /**
     * 新增烟品信息
     */
    int insert(TobaccoGrade tobaccoGrade);

    /**
     * 更新烟品信息
     */
    int update(TobaccoGrade tobaccoGrade);

    /**
     * 根据ID删除烟品信息
     */
    int deleteById(Long id);

    /**
     * 根据牌号名称删除烟品信息
     */
    int deleteByGradeName(String gradeName);
}
