package com.office.yancao.dto;

import lombok.Data;

@Data
public class PostDTO {

    private Long postId;
    private String postName;
    private String supportName;

    private Long sectionId;
    private String sectionName;

    private String line;   // A / B
}

