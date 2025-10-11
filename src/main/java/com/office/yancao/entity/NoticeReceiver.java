package com.office.yancao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeReceiver {
    private Long noticeId;
    private Long userId;
    private int isRead;
    private Date readAt;
    private Date noticeTime;
}
