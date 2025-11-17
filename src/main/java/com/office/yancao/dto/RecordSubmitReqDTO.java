package com.office.yancao.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class RecordSubmitReqDTO {
    private String classes;
    private LocalDate date;
    private Long createdId;
    private List<UsageItem> productionBox;
    private Integer productionBoard;

    private List<UsageItem> boxUsage;
    private List<UsageItem> boardUsage;

    private Integer surplusBox;
    private Integer surplusBoard;
}
