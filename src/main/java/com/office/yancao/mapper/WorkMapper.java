package com.office.yancao.mapper;

import com.office.yancao.entity.Work;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface WorkMapper {

    /**
     * 保存工单
     * @param work 工单实体
     * @return 影响行数
     */
    int saveWork(Work work);

    /**
     * 根据ID查询工单
     * @param id 工单ID
     * @return 工单实体
     */
    Work getWorkById(Long id);

    /**
     * 查询所有工单
     * @return 工单列表
     */
    List<Work> listAllWorks();

    /**
     * 根据用户ID查询工单
     * @param userId 用户ID
     * @return 工单列表
     */
    List<Work> listWorksByUserId(Long userId);

    /**
     * 根据用户ID查询该用户所在班级的今日工单
     * @param userId 用户ID
     * @param startTime 当天开始时间
     * @param endTime 当天结束时间
     * @return 工单列表
     */
    List<Work> selectTodayWorkByUserId(@Param("userId") Integer userId,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);

    /**
     * 根据班级查询今日工单
     * @param classess 班级（甲班/乙班）
     * @param startTime 当天开始时间
     * @param endTime 当天结束时间
     * @return 工单列表
     */
    List<Work> selectTodayWorkByClass(@Param("classes") String classess,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime);

    /**
     * 获取今日工单总数
     * @param classess 班级（甲班/乙班）
     * @param startTime 当天开始时间
     * @param endTime 当天结束时间
     * @return 工单总数
     */
    int countTodayWorkByClass(@Param("classes") String classes,
                              @Param("startTime") Date startTime,
                              @Param("endTime") Date endTime);
}
