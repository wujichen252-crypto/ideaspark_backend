package com.example.project.common.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * PDF工具类
 * 提供PDF创建、读取、合并等功能
 */
public class PdfUtil {

    /**
     * 创建简单PDF文档
     *
     * @param contentList 内容列表
     * @param fileName    文件名
     * @return ByteArrayOutputStream
     * @throws Exception 异常
     */
    public static ByteArrayOutputStream createPdf(List<String> contentList, String fileName) throws Exception {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 创建页面
            PDPage page = new PDPage();
            document.addPage(page);
            
            // 创建内容流
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // 设置字体
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(14.5f);
                
                // 设置起始位置
                contentStream.newLineAtOffset(50, 750);
                
                // 添加标题
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.showText(fileName);
                contentStream.newLine();
                contentStream.newLine();
                
                // 恢复默认字体
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                
                // 添加内容
                for (String content : contentList) {
                    contentStream.showText(content);
                    contentStream.newLine();
                }
            }
            
            document.save(outputStream);
            return outputStream;
        }
    }
    
    /**
     * 读取PDF文档内容
     *
     * @param inputStream PDF输入流
     * @return PDF文本内容
     * @throws Exception 异常
     */
    public static String readPdfContent(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * 合并多个PDF文档
     *
     * @param inputStreams PDF输入流列表
     * @return ByteArrayOutputStream
     * @throws Exception 异常
     */
    public static ByteArrayOutputStream mergePdfs(List<InputStream> inputStreams) throws Exception {
        try (PDDocument mergedDocument = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (InputStream inputStream : inputStreams) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    // 添加所有页面
                    for (PDPage page : document.getPages()) {
                        mergedDocument.addPage(page);
                    }
                }
            }
            
            mergedDocument.save(outputStream);
            return outputStream;
        }
    }
    
    /**
     * 在PDF中添加图片
     *
     * @param inputStream 原PDF输入流
     * @param imageStream 图片输入流
     * @param x           X坐标
     * @param y           Y坐标
     * @param width       宽度
     * @param height      高度
     * @return ByteArrayOutputStream
     * @throws Exception 异常
     */
    public static ByteArrayOutputStream addImageToPdf(InputStream inputStream, InputStream imageStream, float x, float y, float width, float height) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = document.getPage(0);
            
            // 加载图片
            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageStream.readAllBytes(), "image");
            
            // 创建内容流
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                // 添加图片
                contentStream.drawImage(image, x, y, width, height);
            }
            
            document.save(outputStream);
            return outputStream;
        }
    }
    
    /**
     * 在PDF中添加水印
     *
     * @param inputStream 原PDF输入流
     * @param watermark   水印文本
     * @return ByteArrayOutputStream
     * @throws Exception 异常
     */
    public static ByteArrayOutputStream addWatermark(InputStream inputStream, String watermark) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 创建字体
            PDType1Font font = PDType1Font.HELVETICA;
            
            for (PDPage page : document.getPages()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    // 设置透明度
                    contentStream.setNonStrokingColor(new Color(200, 200, 200));
                    contentStream.setFont(font, 36);
                    
                    // 计算页面中心位置
                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();
                    float stringWidth = font.getStringWidth(watermark) / 1000 * 36;
                    float x = (pageWidth - stringWidth) / 2;
                    float y = pageHeight / 2;
                    
                    // 旋转水印
                    contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45), x, y));
                    
                    // 添加水印文本
                    contentStream.showText(watermark);
                }
            }
            
            document.save(outputStream);
            return outputStream;
        }
    }
}
