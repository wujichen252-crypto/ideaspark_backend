package com.ideaspark.project.config;

import com.ideaspark.project.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

