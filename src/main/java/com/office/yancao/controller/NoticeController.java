package com.office.yancao.controller;



import com.office.yancao.dto.NoticeDTO;
import com.office.yancao.dto.NoticeReqDTO;
import com.office.yancao.dto.NoticeRespDto;
import com.office.yancao.entity.Department;
import com.office.yancao.entity.Notice;
import com.office.yancao.entity.ReadStats;
import com.office.yancao.entity.User;
import com.office.yancao.service.NoticeService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;


    @PostMapping(value = "/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Void> publish(NoticeDTO dto) throws IOException {
        //System.out.println(dto.getImages().length);
        noticeService.publishNotice(dto);
        return Result.success();
    }

    @GetMapping("/departments")
    public Result<List<Department>> listDepartments(@RequestParam(value = "id") Long id) {
        return Result.success(noticeService.listDepartments(id));
    }

    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        return Result.success(noticeService.listUsers());
    }

    @GetMapping("/list")
    public Result<List<NoticeRespDto>> listNotices(@RequestParam(value = "userId") Long userId, @RequestParam(value = "startTime") String startTime,
                                                   @RequestParam(value = "endTime") String endTime) {
        return Result.success(noticeService.listNoticesForUser(userId, startTime, endTime));
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
}