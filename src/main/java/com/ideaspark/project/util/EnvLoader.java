package com.ideaspark.project.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 从项目根目录加载 .env 文件，将其中的 KEY=VALUE 写入到 System Properties
 * 支持注释行（以 # 开头）与空行，值按原样读取（不做引号处理）
 */
public final class EnvLoader {
    private EnvLoader() {}

    /**
     * 加载 .env 并注入到 System Properties（如果文件不存在则忽略）
     */
    public static void loadDotEnv() {
        Path envPath = Path.of(System.getProperty("user.dir"), ".env");
        if (!Files.exists(envPath)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(envPath, StandardCharsets.UTF_8);
            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx <= 0) {
                    continue;
                }
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                if (!key.isEmpty()) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
