package com.office.yancao.controller;




import com.office.yancao.dto.FaultReportDTO;
import com.office.yancao.dto.FaultRespList;
import com.office.yancao.entity.FaultReport;
import com.office.yancao.service.FaultService;
import com.office.yancao.untils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/fault")
public class FaultController {

    @Autowired
    private FaultService faultService;

    /**
     * 提交报警（支持图片上传）
     */
    @PostMapping("/report")
    public Result<Long> reportFault(
            @RequestParam Long reporterId,
            @RequestParam String line,
            @RequestParam String section,
            @RequestParam String type,
            @RequestParam String description,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        try {
            FaultReportDTO dto = new FaultReportDTO();
            dto.setReporterId(reporterId);
            dto.setSection(section);
            dto.setLine(line);
            dto.setType(type);
            dto.setDescription(description);

            List<MultipartFile> imageList = (images != null) ? Arrays.asList(images) : null;
            Long faultId = faultService.createFaultReport(dto, imageList);

            return Result.success(faultId); // ✅ 返回成功 + 数据

        } catch (IOException e) {
            return Result.fail(500, "文件上传失败：" + e.getMessage()); // ✅ 返回失败 + 错误信息
        }
    }

    /**
     * 维修工到场
     */
    @PostMapping("/repair")
    public Result<Boolean> markAsArrived(@RequestParam Long id,
                                         @RequestParam String status,
                                         @RequestParam(required = false) MultipartFile[] images) throws IOException {
        List<MultipartFile> imageList = (images != null) ? Arrays.asList(images) : null;
        boolean success = faultService.markAsArrived(id, status, imageList);
        if (success) {
            return Result.success(true); // ✅ 成功
        } else {
            return Result.fail("操作失败，可能报警不存在或状态已变更"); // ✅ 通用失败
        }
    }


    /**
     * 获取报警详情
     */
    @GetMapping("/detail")
    public Result<FaultReportDTO> getDetail(@RequestParam Long id) {
        FaultReportDTO dto = faultService.getFaultDetail(id);
        if (dto != null) {
            return Result.success(dto); // ✅ 返回完整 DTO
        } else {
            return Result.fail(404, "未找到该报警记录");
        }
    }

    /**
     * 根据维修工类别获取报警列表
     */
    @GetMapping("/faultListByType")
    public Result<List<FaultRespList>> faultList(@RequestParam String type, @RequestParam Long userId) {
        System.out.println(type);
        System.out.println(userId);
        return Result.success(faultService.faultListByType(type, userId));
    }
}