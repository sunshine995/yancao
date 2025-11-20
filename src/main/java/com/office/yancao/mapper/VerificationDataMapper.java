package com.office.yancao.mapper;

import com.office.yancao.entity.VerificationData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface VerificationDataMapper {
     void insert(VerificationData data);

     /**
      * 根据 batchId 和 segment 查询一条最新记录
      */
     VerificationData selectLatestByBatchIdAndSegment(@Param("batchId") String batchId, @Param("segment") String segment);
}
