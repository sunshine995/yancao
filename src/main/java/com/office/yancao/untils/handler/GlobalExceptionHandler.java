package com.office.yancao.untils.handler;

import com.office.yancao.untils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 实际项目中建议用日志框架，这里先打印
        e.printStackTrace();
        return Result.fail("系统异常，请联系管理员");
    }
}
