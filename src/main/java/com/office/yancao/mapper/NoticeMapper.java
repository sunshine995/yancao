package com.office.yancao.mapper;

import com.office.yancao.dto.NoticeRespDto;
import com.office.yancao.dto.UnreadUserDTO;
import com.office.yancao.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    void insertNotice(Notice notice);
    void insertReceivers(@Param("receivers") List<NoticeReceiver> receivers);
    void insertReceiver(NoticeReceiver receiver);

    List<UserDepartment> getUsersByDeptId(@Param("departmentId") Long deptId);
    List<User> listUsers();
    List<Department> listDepartments();

    List<NoticeRespDto> listNoticesForUser(@Param("userId") Long userId, @Param("startTime") String startTime, @Param("endTime") String endTime );
    ReadStats getReadStats(@Param("noticeId") Long noticeId);

    Notice getNoticeDetail(Long noticeId);

    void setMarkAsRead(Long noticeId, Long userId);

    /**
     * 查询指定公告的所有未读用户信息
     */
    List<UnreadUserDTO> selectUnreadUsersByNoticeId(@Param("noticeId") Long noticeId);
}
