package com.office.yancao.mapper.admin;

import com.office.yancao.dto.admin.SixsReqDTO;
import com.office.yancao.entity.admin.SixsTaskTemplate;
import com.office.yancao.service.admin.SixsTaskTemplateService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface SixsTaskTemplateMapper {

    int insert(SixsTaskTemplate template);

    int update(SixsTaskTemplate template);

    int deleteById(Integer id);

    // 获取所有模板
    List<SixsTaskTemplate> findAll();

    // 根据分组和班次查询
    List<SixsTaskTemplate> selectByGroupAndShift(@Param("groupName") String groupName,
                                                 @Param("shift") String shift);

    // 根据星期几查询活跃模板
    List<SixsTaskTemplate> selectActiveByWeekday(@Param("weekday") Integer weekday);

    // 根据分组、班次和星期几查询
    SixsTaskTemplate selectByGroupShiftWeekday(@Param("groupName") String groupName,
                                               @Param("shift") String shift,
                                               @Param("weekday") Integer weekday);

    // 批量插入或更新
    int batchInsertOrUpdate(List<SixsTaskTemplate> templates);

    // 更新模板状态
    int updateStatus(@Param("id") Integer id, @Param("isActive") Boolean isActive);

    // 查询所有活跃模板
    List<SixsTaskTemplate> findByCondition(@Param("condition") SixsReqDTO condition);

    SixsTaskTemplate selectTemplateById(@Param("templateId") Integer templateId);

    // 清空表数据（导入前清理）
    int truncateTable();

}
