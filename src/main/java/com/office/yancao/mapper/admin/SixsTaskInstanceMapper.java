package com.office.yancao.mapper.admin;

import com.office.yancao.entity.admin.SixsTaskInstance;
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
    List<SixsTaskInstance> findByEmployeeAndDate(@Param("employeeId") Integer employeeId,
                                                 @Param("scheduledDate") LocalDate scheduledDate);

    // 检查任务实例是否已存在
    int countByTemplateAndEmployeeAndDate(@Param("templateId") Integer templateId,
                                          @Param("employeeId") Integer employeeId,
                                          @Param("scheduledDate") LocalDate scheduledDate);

    // 根据日期查询所有任务实例
    List<SixsTaskInstance> findByDate(@Param("scheduledDate") LocalDate scheduledDate);
}
