package com.office.yancao;

import com.office.yancao.entity.VerificationData;
import com.office.yancao.mapper.VerificationDataMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VerificationDataMapperTest {

    @Autowired
    private VerificationDataMapper verificationDataMapper;

    @Test
    public void testSelectLatestByBatchIdAndSegment() {
        // 测试数据：使用数据库中已有的批次号和段
        String batchId = "LZ(YZP)2510006";
        String segment = "加香机";
        
        // 调用查询方法
        VerificationData result = verificationDataMapper.selectLatestByBatchIdAndSegment(batchId, segment);
        
        // 输出查询结果
        System.out.println("查询结果：");
        if (result != null) {
            System.out.println("ID: " + result.getId());
            System.out.println("Batch ID: " + result.getBatchId());
            System.out.println("Brand: " + result.getBrand());
            System.out.println("Segment: " + result.getSegment());
            System.out.println("Data Count: " + result.getDataCount());
            System.out.println("Operator ID: " + result.getOperatorId());
            System.out.println("Verified Time: " + result.getVerifiedTime());
            System.out.println("Verification Result: " + result.getVerificationResult());
        } else {
            System.out.println("查询结果为null");
        }
        
        // 简单验证结果不为null
        assertNotNull(result);
    }
}