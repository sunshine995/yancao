package com.office.yancao.entity.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class TobaccoInventory {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String shift;

    private Integer incomingLarge;
    private Integer incomingSmall;
    private Integer dosingLarge;
    private Integer dosingSmall;
    private Integer leftoverReturned;
    private Integer leftoverLarge;
    private Integer leftoverSmall;
    private Integer shellLargePulled;
    private Integer shellSmallPulled;
    private Integer shellBalanceLarge; //结余大箱数
    private Integer shellBalanceSmall; //结余小箱数

    private String createdBy;
    private Date createdTime;
    private String updatedBy;
    private Date updatedTime;

    // 用于前端展示的计算字段
    private Integer totalIncoming;     // 总来料箱数
    private Integer totalDosing;       // 总加料箱数
    private Integer dailyLeftoverReturned;       // 总加料箱数
    private Integer totalShellPulled;  // 总拉西库箱皮数
}