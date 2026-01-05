package com.office.yancao.untils;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.MyXWPFDocument;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Slf4j
public class ExportWordUtil {

    /**
     * 通用Word文档导出方法
     * @param templateName 模板文件名
     * @param params 模板参数
     * @param response HTTP响应对象
     * @param outputFileName 输出文件名
     * @throws RuntimeException 导出过程中的异常
     */
    public static void exportWordDocument(String templateName, Map<String, Object> params,
                                          HttpServletResponse response, String outputFileName) {
        MyXWPFDocument doc = null;
        try {
            // 获取模板文档
            doc = getXWPFDocument(templateName);
            if (doc == null) {
                throw new RuntimeException("未能找到模板文件");
            }

            // 使用模板引擎替换内容
            WordExportUtil.exportWord07(doc, params);

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);

            // 输出文档
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                doc.write(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("导出Word文档失败", e);
            throw new RuntimeException("导出Word文档失败: " + e.getMessage());
        } finally {
            // 关闭文档
            if (doc != null) {
                try {
                    doc.close();
                } catch (Exception e) {
                    log.error("关闭文档失败", e);
                }
            }
        }
    }


    public static MyXWPFDocument getXWPFDocument(String templateName) throws IOException {
        // 从classpath获取模板
        InputStream is = null;
        try {
            is = POICacheManager.getFile(templateName);
            if (is == null) {
                throw new FileNotFoundException("模板文件不存在: " + templateName);
            }
            return new MyXWPFDocument(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭输入流失败", e);
                }
            }
        }
    }
}