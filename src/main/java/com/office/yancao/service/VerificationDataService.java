package com.office.yancao.service;

import com.office.yancao.entity.VerificationData;
import com.office.yancao.mapper.VerificationDataMapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationDataService {

    @Autowired
    private VerificationDataMapper verificationDataMapper;

    public void save(VerificationData data) {
        // 设置验证时间为当前时间
        data.setVerifiedTime(LocalDateTime.now());
        verificationDataMapper.insert(data);
    }

    // 根据批次号和段查询一条最新记录
    public VerificationData getLatestByBatchIdAndSegment(String batchId, String segment) {
        return verificationDataMapper.selectLatestByBatchIdAndSegment(batchId, segment);
    }

}
