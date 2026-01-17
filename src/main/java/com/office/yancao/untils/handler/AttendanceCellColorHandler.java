package com.office.yancao.untils.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义单元格写入处理器，用于根据单元格内容设置背景色
 */
public class AttendanceCellColorHandler implements CellWriteHandler {

    // 定义颜色映射关系：内容 -> 颜色索引
    private static final Map<String, Short> COLOR_MAPPING = new HashMap<>();

    // 定义样式缓存，避免重复创建相同的CellStyle
    private final Map<Short, CellStyle> styleCache = new HashMap<>();

    static {
        COLOR_MAPPING.put("白班", IndexedColors.LIGHT_GREEN.getIndex());  // 白班用浅绿色
        COLOR_MAPPING.put("中班", IndexedColors.LIGHT_YELLOW.getIndex()); // 中班用浅黄色
        COLOR_MAPPING.put("夜班", IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex()); // 夜班用浅蓝色
        // 可以根据需要继续添加更多映射...
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {
        // 设置表头样式
        if (isHead) {
            // 设置行高
            row.setHeightInPoints(30);
        }
    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 为表头设置样式
        if (isHead) {
            Workbook workbook = cell.getSheet().getWorkbook();
            CellStyle style = workbook.createCellStyle();
            
            // 设置背景色
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // 设置边框
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            
            // 设置对齐方式
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // 设置字体
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setBold(true);
            style.setFont(font);
            
            cell.setCellStyle(style);
        } else {
            // 为内容单元格设置默认样式
            Workbook workbook = cell.getSheet().getWorkbook();
            CellStyle style = workbook.createCellStyle();
            
            // 设置边框
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            
            // 设置对齐方式
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // 设置字体
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
            
            cell.setCellStyle(style);
        }
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 只处理非表头的单元格
        if (!isHead && cellDataList != null && !cellDataList.isEmpty()) {
            // 获取单元格值
            String cellValueStr = "";
            try {
                cellValueStr = cell.getStringCellValue().trim();
            } catch (IllegalStateException e) {
                // 如果是数字类型，转换为字符串
                if (cell.getCellType() == CellType.NUMERIC) {
                    cellValueStr = String.valueOf(cell.getNumericCellValue());
                }
            }

            // 检查是否需要设置颜色
            if (COLOR_MAPPING.containsKey(cellValueStr)) {
                short targetColorIndex = COLOR_MAPPING.get(cellValueStr);

                // 获取 Workbook
                Workbook workbook = writeSheetHolder.getSheet().getWorkbook();

                // 从缓存中获取或创建样式
                CellStyle targetStyle = styleCache.computeIfAbsent(targetColorIndex, colorIndex -> {
                    CellStyle newStyle = workbook.createCellStyle();
                    // 克隆默认样式
                    newStyle.cloneStyleFrom(cell.getCellStyle());
                    // 设置背景颜色
                    newStyle.setFillForegroundColor(colorIndex);
                    newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    return newStyle;
                });

                // 直接设置单元格样式
                cell.setCellStyle(targetStyle);
            }
        }
    }
}
