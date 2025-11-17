package com.office.yancao.controller;



import com.office.yancao.dto.NoticeDTO;
import com.office.yancao.dto.NoticeReqDTO;
import com.office.yancao.dto.NoticeRespDto;
import com.office.yancao.dto.UpdatePromotionRequest;
import com.office.yancao.entity.*;
import com.office.yancao.service.NoticeService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;


    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody NoticeDTO dto) throws IOException {
        //System.out.println(dto.getType());
        noticeService.publishNotice(dto);
        return Result.success();
    }


    @GetMapping("/list")
    public Result<List<NoticeRespDto>> listNotices(@RequestParam(value = "userId") Long userId) {
        return Result.success(noticeService.listNoticesForUser(userId));
    }

    @GetMapping("/stats/{id}")
    public Result<ReadStats> getStats(@PathVariable Long id) {
        return Result.success(noticeService.getReadStats(id));
    }

    @GetMapping("/detail")
    public Result<Notice> getNoticeDetail(@RequestParam(value = "noticeId") Long noticeId) {
        return Result.success(noticeService.getNoticeDetail(noticeId));
    }

    @PostMapping("/markAsRead")
    public Result<Void> markAsRead(@RequestBody NoticeReqDTO noticeReqDTO) {
        noticeService.markAsRead(noticeReqDTO.getNoticeId(), noticeReqDTO.getUserId());
        return Result.success();
    }

    @GetMapping("/unreadUsers")
    public Result<Map<String, Object>> getUnreadUsers(@RequestParam Long noticeId) {
        return Result.success(noticeService.getUnreadUsers(noticeId));
    }

    @GetMapping("/promotion/list")
    public Result<List<Article>> listPromotion() {
        return Result.success(noticeService.listPromotion());
    }

    @PutMapping("/updatePromotion")
    public Result<Void> updatePromotion(@RequestBody UpdatePromotionRequest request) throws IOException {
        noticeService.updatePromotion(request.getArticleId(), request.getIsBanner());
        return Result.success();
    }


    /**
     * 保存内容
     */
    @PostMapping("/save")
    public Result<Long> saveContent(@RequestBody Article article) {
        try {
            int contentId = noticeService.saveArticle(article);
            return Result.success(Integer.toUnsignedLong(contentId));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    //获取轮播图接口
    @GetMapping("/getArticleBanner")
    public Result<List<Article>> getArticleBanner() {
        return Result.success(noticeService.getArticleBanner());
    }

    //获取轮播图接口
    @GetMapping("/getArticleDetail")
    public Result<Article> getArticleDetail(@RequestParam("id") Long id) {
        System.out.println(id);
        return Result.success(noticeService.getArticleDetail(id));
    }
}