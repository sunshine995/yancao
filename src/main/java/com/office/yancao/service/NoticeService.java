package com.office.yancao.service;

import com.office.yancao.dto.NoticeDTO;
import com.office.yancao.dto.NoticeRespDto;
import com.office.yancao.dto.UnreadUserDTO;
import com.office.yancao.entity.*;
import com.office.yancao.mapper.NoticeMapper;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.mapper.WorkGroupMapper;
import com.office.yancao.service.admin.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    // 新增：注入MQTT服务
    @Autowired
    private MqttService mqttService;


    @Transactional
    public void publishNotice(NoticeDTO dto) throws IOException {
        Notice notice = new Notice();
        notice.setContent(dto.getContent());
        notice.setCreatedBy(dto.getUsername());
        notice.setTitle(dto.getTitle());
        notice.setType(dto.getType());
        // 处理可能为空的图片数组
        String images = (dto.getImages() != null && !dto.getImages().isEmpty())
                ? String.join(",", dto.getImages())
                : "";
        String files = (dto.getFilesUrl() != null && !dto.getFilesUrl().isEmpty())
                ? String.join(",", dto.getFilesUrl())
                : "";
        String originFiles = (dto.getOriginalFileNames() != null && !dto.getOriginalFileNames().isEmpty())
                ? String.join(",", dto.getOriginalFileNames())
                : "";
        String videos = (dto.getVideoUrls() != null && !dto.getVideoUrls().isEmpty())
                ? String.join(",", dto.getVideoUrls())
                : "";
        notice.setFilesUrl(files);
        notice.setFilesName(originFiles);
        notice.setVideoUrl(videos);
        notice.setImages(images);
        noticeMapper.insertNotice(notice);
        List<Long> usersList = new ArrayList<>();
        if (dto.getType().equals("ALL")){
            Long userId = dto.getUserId();
            String userClass = userMapper.getUserClassById(userId);
            usersList = userMapper.getUserByClass(userClass);
        }else if (dto.getType().equals("WORK_GROUP")){
            for (String classes : dto.getRangeIds()){
                usersList.addAll(userMapper.getUserByClass(classes));
            }
        }else if (dto.getType().equals("PARTY_BRANCH")){
            for (String id : dto.getRangeIds()){
                if (id.equals("3")){
                    usersList.addAll(userMapper.getUserByParty("第一党支部", "甲小组"));
                }else if (id.equals("4")){
                    usersList.addAll(userMapper.getUserByParty("第一党支部", "白小组"));
                }else if (id.equals("5")){
                    usersList.addAll(userMapper.getUserByParty("第二党支部", "乙小组"));
                }else{
                    usersList.addAll(userMapper.getUserByParty("第二党支部", "白小组"));
                }
            }
        }else{
            // 根据用户ID和小组名字
            for (String GroupId : dto.getRangeIds()){
                usersList.addAll(workGroupMapper.getUsersById(Long.valueOf(GroupId), dto.getUserId()));
            }
        }
        for (Long userId : usersList){
            NoticeReceiver noticeReceiver = new NoticeReceiver();
            noticeReceiver.setNoticeId(notice.getId());
            noticeReceiver.setUserId(userId);
            noticeMapper.insertReceiver(noticeReceiver);

            // ============ 新增：发送MQTT实时通知 ============
            try {
                sendNoticeNotification(userId, notice);
            } catch (Exception e) {
                // 记录错误但不要影响主流程
                System.out.println("发送MQTT通知失败，用户ID: " + userId + ", 错误: " + e.getMessage());
                System.err.println("发送MQTT通知失败，用户ID: " + userId + ", 错误: " + e.getMessage());
            }
        }
    }




    public List<NoticeRespDto> listNoticesForUser(Long userId) {
        List<NoticeRespDto> res = noticeMapper.listNoticesForUser(userId);
        // return noticeMapper.listNoticesForUser(userId, startTime, endTime);
        return res;
    }

    public ReadStats getReadStats(Long noticeId) {
        return noticeMapper.getReadStats(noticeId);
    }

    public Notice getNoticeDetail(Long noticeId) {
        Notice noticeDetail = noticeMapper.getNoticeDetail(noticeId);
        String imageStr = noticeDetail.getImages();
        String filesStr = noticeDetail.getFilesUrl();
        String fileOrigin = noticeDetail.getFilesName();
        String video = noticeDetail.getVideoUrl();
        List<String> images = (imageStr == null || imageStr.isEmpty())
                ? Collections.emptyList()
                : Arrays.asList(imageStr.split(","));

        List<String> files = (filesStr == null || filesStr.isEmpty())
                ? Collections.emptyList()
                : Arrays.asList(filesStr.split(","));

        List<String> origin = (fileOrigin == null || fileOrigin.isEmpty())
                ? Collections.emptyList()
                : Arrays.asList(fileOrigin.split(","));

        List<String> videos = (video == null || video.isEmpty())
                ? Collections.emptyList()
                : Arrays.asList(video.split(","));
        noticeDetail.setImagesUrl(images);
        noticeDetail.setFileUrls(files);
        noticeDetail.setFileOriginUrls(origin);
        noticeDetail.setVideoUrls(videos);
        return noticeDetail;
    }

    public void markAsRead(Long noticeId, Long userId) {
        noticeMapper.setMarkAsRead(noticeId, userId);
    }

    public Map<String, Object> getUnreadUsers(Long noticeId) {

        // 2. 查询未读用户
        List<UnreadUserDTO> unreadUsers = noticeMapper.selectUnreadUsersByNoticeId(noticeId);

        // 3. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("users", unreadUsers);
        result.put("unreadCount", unreadUsers.size());

        return result;
    }



    public List<Article> listPromotion() {
        List<Article> res = noticeMapper.listPromotion();
        return res;
    }

    public void updatePromotion(Long articleId, Boolean isBanner) {
        if (isBanner){
            noticeMapper.updatePromotion(articleId, 1);
        }else {
            noticeMapper.updatePromotion(articleId, 0);
        }
    }

    public int saveArticle(Article article) {
        if (!article.getCoverImages().isEmpty()){
            String image = article.getCoverImages().get(0);
            System.out.println(image);
            article.setCoverImage(image);
        }
        return noticeMapper.insertArticle(article);
    }

    public List<Article> getArticleBanner() {
        List<Article> res = noticeMapper.getArticleBanner();
        return res;
    }

    /**
     * 向单个用户发送公告通知
     */
    private void sendNoticeNotification(Long userId, Notice notice) {
        System.out.println(userId);
        // 构建通知消息
        Map<String, Object> noticeMsg = new HashMap<>();
        noticeMsg.put("msgId", "NOTICE_" + notice.getId() + "_" + System.currentTimeMillis());
        noticeMsg.put("type", "NOTICE_PUBLISHED");
        noticeMsg.put("priority", "MEDIUM"); // 公告通常设为中等优先级
        noticeMsg.put("title", "新公告通知");
        noticeMsg.put("content", "您收到一条新公告：" + notice.getTitle());
        noticeMsg.put("noticeId", notice.getId());
        noticeMsg.put("noticeTitle", notice.getTitle());
        noticeMsg.put("publisher", notice.getCreatedBy());
        noticeMsg.put("publishTime", new Date());

        // 修复：将 Map.of() 替换为 HashMap 初始化（兼容 Java 8）
        // 1. 构建 action 的 params 子Map
        Map<String, Object> actionParams = new HashMap<>();
        actionParams.put("noticeId", notice.getId());
        // 2. 构建 action 主Map
        Map<String, Object> actionMap = new HashMap<>();
        actionMap.put("type", "OPEN_NOTICE");
        actionMap.put("params", actionParams);
        // 3. 将 actionMap 放入通知消息
        noticeMsg.put("action", actionMap);

        noticeMsg.put("requireAck", true);
        noticeMsg.put("sound", "notice_alert");
        noticeMsg.put("vibrate", true);

        // 发送到用户个人主题
        String topic = "workshop/" + userId + "/notice";
        mqttService.publishMessage(topic, noticeMsg, 1, false);
    }

    public Article getArticleDetail(Long id) {
        return noticeMapper.getArticleDetail(id);
    }
}