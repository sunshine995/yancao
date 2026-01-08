package com.office.yancao.controller.admin;

import com.github.pagehelper.PageInfo;
import com.office.yancao.dto.admin.LearningResourceQueryDTO;
import com.office.yancao.dto.admin.ResourceListByTypeDTO;
import com.office.yancao.entity.admin.LearningResource;
import com.office.yancao.service.admin.LearningResourceService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning/resource")
public class LearningResourceController {

    @Autowired
    private LearningResourceService resourceService;

    /** 新增学习资源 */
    @PostMapping("/save")
    public void save(@RequestBody LearningResource resource) {
        resourceService.save(resource);
    }

    /** 分页查询（后台列表） */
    @GetMapping("/page")
    public Result<PageInfo<LearningResource>> page(LearningResourceQueryDTO learningResourceQueryDTO) {
        return Result.success(resourceService.page(learningResourceQueryDTO));
    }

    /** 获取详情（编辑回显） */
    @GetMapping("/byId")
    public Result<LearningResource> detail(@RequestParam Long id) {
        return Result.success(resourceService.getById(id));
    }

    /** 上架 / 下架 */
    @PutMapping("/status")
    public void updateStatus(@RequestParam Long id,
                             @RequestParam Integer status) {
        resourceService.updateStatus(id, status);
    }

    // 删除学习资源
    @DeleteMapping ("/delete/{id}")
    public void deleteResource(@PathVariable Long id){
        resourceService.deleteResource(id);
    }

    /** 获取详情（编辑回显） */
    @GetMapping("/getByType")
    public Result<List<ResourceListByTypeDTO>> getResourceList(@RequestParam Long id) {
        return Result.success(resourceService.getResourceList(id));
    }
}

