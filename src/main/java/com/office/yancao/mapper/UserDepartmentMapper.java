package com.office.yancao.mapper;

import com.office.yancao.entity.Department;
import com.office.yancao.entity.UserDepartment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface UserDepartmentMapper {
    UserDepartment selectDepartmentId(@Param("userId") Long id);

    // 传入一个id找到该id所有子id
    List<Long> getDirectChildDeptIds(@Param("parentId") Long parentId);

    // 根据部门id查询所有用户
    List<Long> getUserIdsByDeptIds(@Param("departmentIds") Collection<Long> deptIds);

    List<Department> getDirectChildren(@Param("parentId") Long parentId);

}
