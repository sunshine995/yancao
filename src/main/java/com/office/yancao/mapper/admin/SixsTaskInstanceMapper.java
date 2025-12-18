package com.office.yancao.mapper.admin;

import com.office.yancao.entity.admin.SixsTaskInstance;
import com.office.yancao.dto.admin.SixTaskInstanceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SixsTaskInstanceMapper {

    // 插入任务实例
    int insert(SixsTaskInstance instance);

    // 批量插入任务实例
    int batchInsert(@Param("instances") List<SixsTaskInstance> instances);

    // 根据员工和日期查询任务实例
    SixsTaskInstance findByEmployeeAndDate(@Param("employeeId") Long employeeId,
                                                 @Param("scheduledDate") LocalDate scheduledDate);

    // 检查任务实例是否已存在
    int countByTemplateAndEmployeeAndDate(@Param("templateId") Integer templateId,
                                          @Param("employeeId") Long employeeId,
                                          @Param("scheduledDate") LocalDate scheduledDate);

    // 根据日期查询所有任务实例
    List<SixsTaskInstance> findByDate(@Param("scheduledDate") LocalDate scheduledDate);

    void updateById(SixsTaskInstance sixsTaskInstance);

    SixsTaskInstance selectById(@Param("id") Integer id);

    // 查询每日6s任务
    List<SixTaskInstanceDTO> selectSpotCheckTasks();

    SixTaskInstanceDTO getSixTaskById(@Param("taskId") Long taskId);
}
