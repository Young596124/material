package com.material.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 物料出入库记录详情 Excel 导出工具（基于 Apache POI）
 * 主表数据与子表数据分别写入一个工作表；子表工作表首列为对应的主表编码
 */
public class MaterialRecordExcelUtil
{
    /**
     * 导出记录详情 Excel（主表 + 子表两个工作表）
     *
     * @param mainSheetName   主表工作表名称
     * @param mainHeaders     主表表头
     * @param mainRows        主表数据行
     * @param detailSheetName 子表工作表名称
     * @param detailHeaders   子表表头
     * @param detailRows      子表数据行（已含主表编码列）
     * @param fileName        导出文件名
     * @param response        HTTP 响应
     */
    public static void export(String mainSheetName, String[] mainHeaders, List<Object[]> mainRows,
                              String detailSheetName, String[] detailHeaders, List<Object[]> detailRows,
                              String fileName, HttpServletResponse response) throws IOException
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            writeSheet(workbook, mainSheetName, mainHeaders, mainRows);
            writeSheet(workbook, detailSheetName, detailHeaders, detailRows);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String encodeName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodeName);
            try (OutputStream out = response.getOutputStream())
            {
                workbook.write(out);
                out.flush();
            }
        }
    }

    /**
     * 写入单个工作表（表头 + 数据）
     */
    private static void writeSheet(Workbook workbook, String sheetName, String[] headers, List<Object[]> rows)
    {
        Sheet sheet = workbook.createSheet(sheetName);

        // 表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 内容样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // 写表头
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++)
        {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 写数据
        if (rows != null)
        {
            for (int r = 0; r < rows.size(); r++)
            {
                Object[] values = rows.get(r);
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < values.length; c++)
                {
                    setCell(row, c, values[c], cellStyle);
                }
            }
        }

        // 自动列宽
        for (int i = 0; i < headers.length; i++)
        {
            sheet.autoSizeColumn(i);
            int autoWidth = sheet.getColumnWidth(i);
            // 限制最小宽度，避免空列太窄
            int minWidth = 12 * 256;
            sheet.setColumnWidth(i, Math.max(autoWidth + 1024, minWidth));
        }
    }

    private static void setCell(Row row, int col, Object value, CellStyle style)
    {
        Cell cell = row.createCell(col);
        if (value == null)
        {
            cell.setCellValue("");
        }
        else if (value instanceof Number)
        {
            cell.setCellValue(((Number) value).doubleValue());
        }
        else
        {
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(style);
    }
}
