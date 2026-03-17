package com.ideaspark.project.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    /**
     * 成功响应（status/message/data）
     */
    public static ResponseEntity<?> ok(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /**
     * 成功响应（仅 message）
     */
    public static ResponseEntity<?> ok(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 成功响应（仅 data）
     */
    public static ResponseEntity<?> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /**
     * 成功响应（无内容）
     */
    public static ResponseEntity<?> success() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * 创建成功响应（201）
     */
    public static ResponseEntity<?> created(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "created");
        response.put("data", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 错误响应
     */
    public static ResponseEntity<?> error(String message, int statusCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", statusCode);
        response.put("message", message);
        return ResponseEntity.status(statusCode).body(response);
    }

    /**
     * 错误响应（带数据）
     */
    public static ResponseEntity<?> error(String message, int statusCode, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", statusCode);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.status(statusCode).body(response);
    }
}
