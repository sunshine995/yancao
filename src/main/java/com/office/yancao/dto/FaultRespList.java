package com.office.yancao.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FaultRespList {
    private Long id;
    private Long reporterId;
    private String reporterName;
    private String line;
    private String section;
    private String type;
    private String description;
    private String status;
    private LocalDateTime reportTime;

}
