package com.example.project.common.utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Markdown工具类
 * 提供Markdown转HTML等功能
 */
public class MarkdownUtil {

    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    /**
     * 将Markdown文本转换为HTML
     *
     * @param markdown Markdown文本
     * @return HTML文本
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        
        Node document = parser.parse(markdown);
        return htmlRenderer.render(document);
    }
    
    /**
     * 将HTML文本转换为纯文本（移除标签）
     *
     * @param html HTML文本
     * @return 纯文本
     */
    public static String htmlToText(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        // 移除HTML标签
        return html.replaceAll("<[^>]*>", "")
                // 移除HTML实体
                .replaceAll("&[a-zA-Z]+;", " ")
                // 移除多余空格
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    /**
     * 将Markdown文本转换为纯文本
     *
     * @param markdown Markdown文本
     * @return 纯文本
     */
    public static String markdownToText(String markdown) {
        String html = markdownToHtml(markdown);
        return htmlToText(html);
    }
}
