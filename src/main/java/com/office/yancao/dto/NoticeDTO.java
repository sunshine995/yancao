package com.office.yancao.dto;

import lombok.Data;
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
    private String username;
    private Long userId;
    private List<String> images; // 上传图片
    private List<String> filesUrl; // 文件url
    private List<String> originalFileNames; // 文件原始名字
    private List<String> videoUrls; // 视频URl

    private List<String> rangeIds;
}
