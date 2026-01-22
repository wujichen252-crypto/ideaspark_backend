package com.ideaspark.project.service;

import com.aliyun.oss.OSS;
import com.ideaspark.project.config.OssProperties;
import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.response.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "oss", name = "enabled", havingValue = "true")
public class OssService {

    private final OSS oss;
    private final OssProperties ossProperties;

    /**
     * 上传文件到 OSS，并返回对象 Key
     */
    public String upload(MultipartFile file) {
        return upload(file, null);
    }

    /**
     * 上传文件到 OSS（可指定业务目录），并返回对象 Key
     */
    public String upload(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = extractSuffix(originalFilename);

        String baseDir = normalizeDir(ossProperties.getBaseDir());
        String bizDir = normalizeDir(dir);
        String key = joinPath(baseDir, bizDir, UUID.randomUUID().toString().replace("-", "") + suffix);

        try (InputStream inputStream = file.getInputStream()) {
            oss.putObject(ossProperties.getBucket(), key, inputStream);
            return key;
        } catch (Exception e) {
            throw new BusinessException("OSS 上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件并返回详细信息
     */
    public FileUploadResponse uploadFile(MultipartFile file) {
        // 1. 生成日期目录 (yyyy/MM/dd)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 2. 上传文件
        String key = upload(file, datePath);

        // 3. 构建完整 URL
        String endpoint = ossProperties.getEndpoint();
        String bucket = ossProperties.getBucket();

        // 处理 endpoint，去掉协议头用于拼接
        String domain = endpoint.replace("https://", "").replace("http://", "");
        String url = "https://" + bucket + "." + domain + "/" + key;

        // 4. 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        return FileUploadResponse.builder()
                .url(url)
                .path(key)
                .fileName(originalFilename)
                .fileExtension(extension)
                .build();
    }

    /**
     * 拼接路径片段并规范化分隔符
     */
    private String joinPath(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            String p = part.replace("\\", "/");
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '/') {
                sb.append('/');
            }
            sb.append(trimSlashes(p));
        }
        return sb.toString();
    }

    /**
     * 规范化目录字符串
     */
    private String normalizeDir(String dir) {
        if (dir == null) {
            return null;
        }
        String d = dir.trim().replace("\\", "/");
        d = trimSlashes(d);
        return d.isEmpty() ? null : d;
    }

    /**
     * 获取文件后缀名（含点），不存在则返回空字符串
     */
    private String extractSuffix(String filename) {
        if (filename == null) {
            return "";
        }
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        String suffix = filename.substring(idx);
        return suffix.length() > 16 ? "" : suffix;
    }

    /**
     * 去掉首尾斜杠
     */
    private String trimSlashes(String value) {
        String v = Objects.requireNonNullElse(value, "");
        while (v.startsWith("/")) {
            v = v.substring(1);
        }
        while (v.endsWith("/")) {
            v = v.substring(0, v.length() - 1);
        }
        return v;
    }
}

