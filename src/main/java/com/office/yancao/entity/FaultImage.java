package com.office.yancao.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FaultImage {
    private Long id;
    private Long faultId;
    private Long noticeId;
    private String imageUrl;
    private String imageType; // initial, arrival, repair
    private LocalDateTime uploadTime;
    private LocalDateTime createdAt;
}
