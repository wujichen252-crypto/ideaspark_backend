package com.example.project.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 * 提供Excel导入导出功能
 */
public class ExcelUtil {

    /**
     * 导出Excel文件
     *
     * @param dataList 数据列表
     * @param clazz    数据类型
     * @param fileName 文件名
     * @param <T>      泛型
     * @return ByteArrayOutputStream
     * @throws Exception 异常
     */
    public static <T> ByteArrayOutputStream exportExcel(List<T> dataList, Class<T> clazz, String fileName) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 创建工作表
            Sheet sheet = workbook.createSheet(fileName);
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            Field[] fields = clazz.getDeclaredFields();
            
            // 设置标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // 设置数据样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // 填充标题行
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                String fieldName = fields[i].getName();
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fieldName);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // 填充数据行
            for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
                T data = dataList.get(rowIndex);
                Row dataRow = sheet.createRow(rowIndex + 1);
                
                for (int cellIndex = 0; cellIndex < fields.length; cellIndex++) {
                    Field field = fields[cellIndex];
                    field.setAccessible(true);
                    Object value = field.get(data);
                    
                    Cell cell = dataRow.createCell(cellIndex);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                    cell.setCellStyle(dataStyle);
                    sheet.autoSizeColumn(cellIndex);
                }
            }
            
            workbook.write(outputStream);
            return outputStream;
        }
    }
    
    /**
     * 导入Excel文件
     *
     * @param inputStream Excel输入流
     * @param clazz       数据类型
     * @param <T>         泛型
     * @return 数据列表
     * @throws Exception 异常
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) throws Exception {
        List<T> dataList = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return dataList;
            }
            
            Field[] fields = clazz.getDeclaredFields();
            int headerRowNum = sheet.getFirstRowNum();
            Row headerRow = sheet.getRow(headerRowNum);
            
            if (headerRow == null) {
                return dataList;
            }
            
            int firstDataRowNum = headerRowNum + 1;
            int lastRowNum = sheet.getLastRowNum();
            
            for (int rowIndex = firstDataRowNum; rowIndex <= lastRowNum; rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);
                if (dataRow == null) {
                    continue;
                }
                
                T data = clazz.getDeclaredConstructor().newInstance();
                
                for (int cellIndex = 0; cellIndex < fields.length; cellIndex++) {
                    Cell cell = dataRow.getCell(cellIndex);
                    if (cell == null) {
                        continue;
                    }
                    
                    Field field = fields[cellIndex];
                    field.setAccessible(true);
                    
                    Object value = getCellValue(cell);
                    if (value != null) {
                        if (field.getType() == String.class) {
                            field.set(data, value.toString());
                        } else if (field.getType() == Integer.class) {
                            field.set(data, Integer.valueOf(value.toString()));
                        } else if (field.getType() == Long.class) {
                            field.set(data, Long.valueOf(value.toString()));
                        } else if (field.getType() == Double.class) {
                            field.set(data, Double.valueOf(value.toString()));
                        } else if (field.getType() == Boolean.class) {
                            field.set(data, Boolean.valueOf(value.toString()));
                        }
                    }
                }
                
                dataList.add(data);
            }
        }
        
        return dataList;
    }
    
    /**
     * 获取单元格值
     *
     * @param cell 单元格
     * @return Object
     */
    private static Object getCellValue(Cell cell) {
        Object value = null;
        CellType cellType = cell.getCellType();
        
        switch (cellType) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            default:
                value = null;
        }
        
        return value;
    }
}
