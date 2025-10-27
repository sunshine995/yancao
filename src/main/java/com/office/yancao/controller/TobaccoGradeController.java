package com.office.yancao.controller;


import com.office.yancao.entity.TobaccoGrade;
import com.office.yancao.service.TobaccoGradeService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tobacco-grade")
public class TobaccoGradeController {

    @Autowired
    private TobaccoGradeService tobaccoGradeService;

    /**
     * 查询所有烟品信息
     */
    @GetMapping("/all")
    public Result<List<TobaccoGrade>> getAllTobaccoGrades() {
        List<TobaccoGrade> tobaccoGrades = tobaccoGradeService.findAll();
        return Result.success(tobaccoGrades);
    }

    /**
     * 根据ID查询烟品信息
     */
    @GetMapping("/{id}")
    public Result<TobaccoGrade> getTobaccoGradeById(@PathVariable Long id) {
        TobaccoGrade tobaccoGrade = tobaccoGradeService.findById(id);
        if (tobaccoGrade != null) {
            return Result.success(tobaccoGrade);
        } else {
            return Result.fail("未找到指定ID的烟品信息");
        }
    }

    /**
     * 根据牌号名称查询烟品信息
     */
    @GetMapping("/grade/{gradeName}")
    public Result<List<TobaccoGrade>> getTobaccoGradesByGradeName(@PathVariable String gradeName) {
        List<TobaccoGrade> tobaccoGrades = tobaccoGradeService.findByGradeName(gradeName);
        return Result.success(tobaccoGrades);
    }

    /**
     * 新增烟品信息
     */
    @PostMapping
    public Result<String> addTobaccoGrade(@RequestBody TobaccoGrade tobaccoGrade) {
        int result = tobaccoGradeService.insert(tobaccoGrade);
        if (result > 0) {
            return Result.success("烟品信息添加成功");
        } else {
            return Result.fail("烟品信息添加失败");
        }
    }

    /**
     * 更新烟品信息
     */
    @PutMapping("/{id}")
    public Result<String> updateTobaccoGrade(@PathVariable Long id, @RequestBody TobaccoGrade tobaccoGrade) {
        tobaccoGrade.setId(id);
        int result = tobaccoGradeService.update(tobaccoGrade);
        if (result > 0) {
            return Result.success("烟品信息更新成功");
        } else {
            return Result.fail("烟品信息更新失败");
        }
    }

    /**
     * 删除烟品信息（根据ID）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteTobaccoGrade(@PathVariable Long id) {
        int result = tobaccoGradeService.deleteById(id);
        if (result > 0) {
            return Result.success("烟品信息删除成功");
        } else {
            return Result.fail("烟品信息删除失败");
        }
    }

    /**
     * 删除烟品信息（根据牌号名称）
     */
    @DeleteMapping("/grade/{gradeName}")
    public Result<String> deleteTobaccoGradeByGradeName(@PathVariable String gradeName) {
        int result = tobaccoGradeService.deleteByGradeName(gradeName);
        if (result > 0) {
            return Result.success("烟品信息删除成功");
        } else {
            return Result.fail("烟品信息删除失败");
        }
    }
}



