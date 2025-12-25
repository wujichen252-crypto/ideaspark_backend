package com.ideaspark.project.exception;

public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 创建业务异常（默认 400）
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 创建业务异常（自定义 code）
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取业务错误码
     */
    public int getCode() {
        return code;
    }
}

