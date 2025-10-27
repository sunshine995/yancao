package com.office.yancao.service;


import com.office.yancao.entity.TobaccoGrade;
import com.office.yancao.mapper.TobaccoGradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TobaccoGradeService {

    @Autowired
    private TobaccoGradeMapper tobaccoGradeMapper;

    /**
     * 查询所有烟品信息
     */
    public List<TobaccoGrade> findAll() {
        return tobaccoGradeMapper.findAll();
    }

    /**
     * 根据ID查询烟品信息
     */
    public TobaccoGrade findById(Long id) {
        return tobaccoGradeMapper.findById(id);
    }

    /**
     * 根据牌号名称查询烟品信息
     */
    public List<TobaccoGrade> findByGradeName(String gradeName) {
        return tobaccoGradeMapper.findByGradeName(gradeName);
    }

    /**
     * 新增烟品信息
     */
    public int insert(TobaccoGrade tobaccoGrade) {
        return tobaccoGradeMapper.insert(tobaccoGrade);
    }

    /**
     * 更新烟品信息
     */
    public int update(TobaccoGrade tobaccoGrade) {
        return tobaccoGradeMapper.update(tobaccoGrade);
    }

    /**
     * 根据ID删除烟品信息
     */
    public int deleteById(Long id) {
        return tobaccoGradeMapper.deleteById(id);
    }

    /**
     * 根据牌号名称删除烟品信息
     */
    public int deleteByGradeName(String gradeName) {
        return tobaccoGradeMapper.deleteByGradeName(gradeName);
    }
}
