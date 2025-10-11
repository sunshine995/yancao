package com.office.yancao.mapper;

import com.office.yancao.entity.FaultImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FaultImageMapper {
    int insert(FaultImage image);

    int insertNoticeImage(FaultImage image);

    List<String> findByFaultIdAndType(@Param("faultId") Long faultId, @Param("imageType") String imageType);
    List<String> findByNoticeId(@Param("noticeId") Long noticeId);


}
