package com.material.common.utils;

import com.material.modules.material.domain.WarehouseMaterialBase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 1F线边仓物料基表 Excel 导入导出工具（基于 Apache POI）
 * 模板列：物料编码、物料名称、需求人、项目系统、来源类型、货位、入库总数、剩余数量、出库数量
 */
public class WarehouseBaseExcelUtil
{


    private static final String[] HEADERS = {
            "物料编码","物料名称", "需求人", "项目系统", "来源类型","仓库",
            "货位", "入库总数", "剩余数量", "出库数量","特殊说明","创建人","创建时间","修改人","修改时间"
    };

    private static final String[] HEADER = {
            "物料名称", "需求人", "项目系统", "来源类型","仓库",
            "货位", "入库总数", "剩余数量", "出库数量","特殊说明"
    };

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    /**
     * 下载导入模板（仅表头 + 一行示例）
     */
    public static void downloadTemplate(HttpServletResponse response) throws IOException
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            Sheet sheet = workbook.createSheet("物料基表导入模板");

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // 写表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADER.length; i++)
            {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADER[i]);
                cell.setCellStyle(headerStyle);
            }

            // 写一行示例数据（用于引导用户填写格式）
            CellStyle sampleStyle = workbook.createCellStyle();
            Font sampleFont = workbook.createFont();
            sampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            sampleStyle.setFont(sampleFont);
            Row sampleRow = sheet.createRow(1);
            String[] sample = {"示例物料", "张三", "项目A系统", "工装","1F线边仓", "A-01-01", "0", "0", "0","特殊说明"};
            for (int i = 0; i < sample.length; i++)
            {
                Cell cell = sampleRow.createCell(i);
                cell.setCellValue(sample[i]);
                cell.setCellStyle(sampleStyle);
            }

            // 自动列宽
            for (int i = 0; i < HEADER.length; i++)
            {
                sheet.autoSizeColumn(i);
                int autoWidth = sheet.getColumnWidth(i);
                // 限制最小宽度，避免空列太窄
                int minWidth = 12 * 256;
                sheet.setColumnWidth(i, Math.max(autoWidth + 1024, minWidth));
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("物料基表导入模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            try (OutputStream out = response.getOutputStream())
            {
                workbook.write(out);
                out.flush();
            }
        }
    }

    /**
     * 导出物料基表 Excel
     */
    public static void export(List<WarehouseMaterialBase> list, HttpServletResponse response) throws IOException
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            Sheet sheet = workbook.createSheet("物料基表");

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
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
            for (int i = 0; i < HEADERS.length; i++)
            {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 写数据
            if (list != null)
            {
                for (int r = 0; r < list.size(); r++)
                {
                    WarehouseMaterialBase b = list.get(r);
                    Row row = sheet.createRow(r + 1);
                    setCell(row, 0, b.getMaterialCode(), cellStyle);
                    setCell(row, 1, b.getMaterialName(), cellStyle);
                    setCell(row, 2, b.getApplicantName(), cellStyle);
                    setCell(row, 3, b.getProjectSystem(), cellStyle);
                    setCell(row, 4, b.getSourceType(), cellStyle);
                    setCell(row, 5, b.getWarehouseName(), cellStyle);
                    setCell(row, 6, b.getLocation(), cellStyle);
                    setCell(row, 7, b.getTotalInbound(), cellStyle);
                    setCell(row, 8, b.getRemainingQuantity(), cellStyle);
                    setCell(row, 9, b.getOutboundQuantity(), cellStyle);
                    setCell(row, 10, b.getSpecialNote(), cellStyle);
                    setCell(row, 11, b.getCreateBy(), cellStyle);
                    setCell(row, 12, FORMATTER.format(b.getCreateTime().toInstant()), cellStyle);
                    setCell(row, 13, b.getUpdateBy(), cellStyle);
                    setCell(row, 14, FORMATTER.format(b.getUpdateTime().toInstant()), cellStyle);
                }
            }

            // 自动列宽
            for (int i = 0; i < HEADERS.length; i++)
            {
                sheet.autoSizeColumn(i);
                int autoWidth = sheet.getColumnWidth(i);
                // 限制最小宽度，避免空列太窄
                int minWidth = 12 * 256;
                sheet.setColumnWidth(i, Math.max(autoWidth + 1024, minWidth));
            }

            // 响应输出
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("物料基表.xlsx", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            try (OutputStream out = response.getOutputStream())
            {
                workbook.write(out);
                out.flush();
            }
        }
    }

    /**
     * 导入物料基表 Excel
     */
    public static List<WarehouseMaterialBase> importExcel(MultipartFile file) throws IOException
    {
        List<WarehouseMaterialBase> result = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is))
        {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();
            for (int r = 1; r <= lastRow; r++)
            {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                WarehouseMaterialBase b = new WarehouseMaterialBase();
                b.setMaterialName(getCellString(row, 0));
                b.setApplicantName(getCellString(row, 1));
                b.setProjectSystem(getCellString(row, 2));
                b.setSourceType(getCellString(row, 3));
                b.setWarehouseName(getCellString(row, 4));
                b.setLocation(getCellString(row, 5));
                b.setTotalInbound(getCellInt(row, 6));
                b.setRemainingQuantity(getCellInt(row, 7));
                b.setOutboundQuantity(getCellInt(row, 8));
                b.setSpecialNote(getCellString(row, 9));
                result.add(b);
            }
        }
        return result;
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

    private static String getCellString(Row row, int col)
    {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String v = cell.getStringCellValue();
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }

    private static Integer getCellInt(Row row, int col)
    {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC)
        {
            return (int) cell.getNumericCellValue();
        }
        cell.setCellType(CellType.STRING);
        String v = cell.getStringCellValue();
        if (v == null || v.trim().isEmpty()) return null;
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
