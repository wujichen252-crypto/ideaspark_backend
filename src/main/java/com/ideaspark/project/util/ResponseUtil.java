package com.ideaspark.project.util;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    /**
     * 成功响应（status/message/data）
     */
    public static ResponseEntity<?> ok(String message, Object data) {
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", message,
                "data", data
        ));
    }

    /**
     * 成功响应（仅 message）
     */
    public static ResponseEntity<?> ok(String message) {
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", message
        ));
    }
}

