package com.office.yancao.service;

import com.office.yancao.dto.NoticeDTO;
import com.office.yancao.dto.NoticeRespDto;
import com.office.yancao.dto.UnreadUserDTO;
import com.office.yancao.entity.*;
import com.office.yancao.mapper.NoticeMapper;
import com.office.yancao.mapper.UserMapper;
import com.office.yancao.mapper.WorkGroupMapper;
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

    public Article getArticleDetail(Long id) {
        return noticeMapper.getArticleDetail(id);
    }
}