package com.ideaspark.project.config;

import com.ideaspark.project.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", ex.getCode(),
                "message", ex.getMessage()
        ));
    }

    /**
     * 处理资源不存在异常 (404)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "message", "请求的资源不存在: " + ex.getResourcePath()
        ));
    }

    /**
     * 处理请求方法不支持异常 (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(
                "status", 405,
                "message", "不支持当前请求方法: " + ex.getMethod()
        ));
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "服务器内部错误: " + ex.getMessage()
        ));
    }
}

