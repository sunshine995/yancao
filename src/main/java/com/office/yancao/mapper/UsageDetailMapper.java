package com.office.yancao.mapper;

import com.office.yancao.entity.UsageDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UsageDetailMapper {
    int batchInsert(List<UsageDetail> list);
}
