package com.example.project.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于封装业务逻辑中出现的异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 构造方法
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法，使用默认错误码
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this(500, message);
    }

    /**
     * 构造方法，使用默认错误码和消息
     */
    public BusinessException() {
        this(500, "业务处理失败");
    }

}