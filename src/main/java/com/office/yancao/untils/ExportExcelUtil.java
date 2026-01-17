package com.office.yancao.untils;

import com.alibaba.excel.EasyExcel;
import com.office.yancao.untils.handler.AttendanceCellColorHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExportExcelUtil {

    /**
     * 导出考勤报表Excel
     * @param response HTTP响应对象
     * @param fileName 文件名
     * @param sheetName 工作表名
     * @param dataList 数据列表
     * @param dateList 日期列表
     * @throws RuntimeException 导出过程中的异常
     */
    public static void exportAttendanceExcel(HttpServletResponse response, String fileName, String sheetName,
                                             List<List<Object>> dataList, List<LocalDate> dateList) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 构建表头
            List<List<String>> head = buildAttendanceHead(dateList);

            // 获取输出流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 写入Excel
                EasyExcel.write(outputStream)
                        .head(head)
                        .sheet(sheetName)
                        .registerWriteHandler(new AttendanceCellColorHandler()) // 注册自定义颜色处理器
                        .doWrite(dataList);

                outputStream.flush();
            }
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }



    /**
     * 构建考勤报表表头
     * @param dateList 日期列表
     * @return 表头列表
     */
    private static List<List<String>> buildAttendanceHead(List<LocalDate> dateList) {
        List<List<String>> head = new java.util.ArrayList<>();
        
        // 固定表头
        head.add(java.util.Arrays.asList("序号"));
        head.add(java.util.Arrays.asList("姓名"));
        head.add(java.util.Arrays.asList("班组"));
        
        // 动态日期表头
        for (LocalDate date : dateList) {
            head.add(java.util.Arrays.asList(date.toString()));
        }
        
        // 添加出勤天数和中班天数表头
        head.add(java.util.Arrays.asList("出勤天数"));
        head.add(java.util.Arrays.asList("夜餐"));
        
        return head;
    }
}
