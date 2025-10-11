package com.office.yancao.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class NoticeDTO {
    private String title;
    private String content;
    private String type; // ALL, DEPT, SELECTED
    private List<Long> selectedUserIds;
    private List<Long> selectedDeptIds;
    private String username;
    private Long userId;
    private MultipartFile[] images; // 上传图片
}
