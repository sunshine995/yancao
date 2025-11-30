package com.office.yancao.dto;

import lombok.Getter;
import lombok.Setter;


import java.util.List;
@Getter
@Setter
public class FaultUpDto {
    private Long faultId;
    private String status;
    private String repairNotes;
    private List<String> images;
    private List<String> processImageUrls;
    private List<String> resultImageUrls;
}
