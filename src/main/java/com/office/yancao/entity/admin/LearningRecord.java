package com.office.yancao.entity.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearningRecord {

    private Long id;
    private Long userId;
    private Long resourceId;
    private Integer studyTime; // 秒
    private Integer completed;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

