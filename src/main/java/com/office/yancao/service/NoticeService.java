package com.office.yancao.service;

import com.office.yancao.dto.NoticeDTO;
import com.office.yancao.dto.NoticeRespDto;
import com.office.yancao.dto.UnreadUserDTO;
import com.office.yancao.entity.*;
import com.office.yancao.mapper.FaultImageMapper;
import com.office.yancao.mapper.NoticeMapper;
import com.office.yancao.mapper.UserDepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.base-url}")
    private String baseUrl;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserDepartmentMapper userDepartmentMapper;

    @Autowired
    private FaultImageMapper faultImageMapper;

    public void publishNotice(NoticeDTO dto) throws IOException {
        Date date = new Date();
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setType(dto.getType());
        notice.setCreatedBy(dto.getUsername());
        notice.setCreatedAt(date);
        notice.setIsDeleted(false);
        noticeMapper.insertNotice(notice);
        List<MultipartFile> images = new ArrayList<>();
        if (dto.getImages() != null) {
            images = Arrays.asList(dto.getImages());
        } else {
            images = new ArrayList<>(); // 创建空列表而不是 null
        }
        // 2. 保存图片
        if (images != null && !images.isEmpty()) {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadPath + fileName);
                    Files.copy(file.getInputStream(), path);

                    FaultImage image = new FaultImage();
                    image.setNoticeId(notice.getId());
                    image.setImageUrl(baseUrl + fileName);
                    faultImageMapper.insertNoticeImage(image);
                }
            }
        }


        NoticeReceiver noticeReceiver = new NoticeReceiver();
        noticeReceiver.setNoticeId(notice.getId());
        noticeReceiver.setNoticeTime(date);
        noticeReceiver.setIsRead(0);
        noticeReceiver.setUserId(dto.getUserId());
        // noticeMapper.insertReceiver(noticeReceiver);

        Set<Long> targetUserIds = new HashSet<>();

        Set<Long> result = new HashSet<>();

        if ("ALL".equals(dto.getType())) {
            UserDepartment userDepartment = userDepartmentMapper.selectDepartmentId(dto.getUserId());
            collectChildIds(userDepartment.getDepartmentId(), result);
            List<Long> userIdsByDeptIds = userDepartmentMapper.getUserIdsByDeptIds(result);
            // List<User> users = noticeMapper.listUsers();
            targetUserIds.addAll(userIdsByDeptIds);
        }
        else if ("DEPT".equals(dto.getType()) && dto.getSelectedDeptIds() != null) {
            for (Long deptId : dto.getSelectedDeptIds()) {
                List<UserDepartment> userDepartments = noticeMapper.getUsersByDeptId(deptId);
                targetUserIds.addAll(userDepartments.stream().map(UserDepartment::getUserId).collect(Collectors.toList()));
            }
            noticeMapper.insertReceiver(noticeReceiver);
        }
        else if ("SELECTED".equals(dto.getType()) && dto.getSelectedUserIds() != null) {
            targetUserIds.addAll(dto.getSelectedUserIds());
            noticeMapper.insertReceiver(noticeReceiver);
        }

        if (!targetUserIds.isEmpty()) {
            List<NoticeReceiver> receivers = targetUserIds.stream()
                    .distinct()
                    .map(userId -> {
                        NoticeReceiver r = new NoticeReceiver();
                        r.setNoticeId(notice.getId());
                        r.setUserId(userId);
                        r.setIsRead(0);
                        r.setNoticeTime(date);
                        return r;
                    }).collect(Collectors.toList());
            noticeMapper.insertReceivers(receivers);
        }
    }

    public List<Department> listDepartments(long id) {
        UserDepartment userDepartment = userDepartmentMapper.selectDepartmentId(id);
        return userDepartmentMapper.getDirectChildren(userDepartment.getDepartmentId());
    }

    public List<User> listUsers() {
        return noticeMapper.listUsers();
    }

    public List<NoticeRespDto> listNoticesForUser(Long userId, String startTime, String endTime ) {
        List<NoticeRespDto> res = noticeMapper.listNoticesForUser(userId, startTime, endTime);
        // return noticeMapper.listNoticesForUser(userId, startTime, endTime);
        return res;
    }

    public ReadStats getReadStats(Long noticeId) {
        return noticeMapper.getReadStats(noticeId);
    }

    public Notice getNoticeDetail(Long noticeId) {
        Notice noticeDetail = noticeMapper.getNoticeDetail(noticeId);
        List<String> images = faultImageMapper.findByNoticeId(noticeId);
        noticeDetail.setImages(images);
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

    private void collectChildIds(Long deptId, Set<Long> result) {
        result.add(deptId); // 先把自己加进去

        // 查询直接子部门
        List<Long> children = userDepartmentMapper.getDirectChildDeptIds(deptId);
        for (Long childId : children) {
            collectChildIds(childId, result); // 递归
        }
    }
}