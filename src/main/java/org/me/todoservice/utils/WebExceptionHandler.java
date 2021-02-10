package org.me.todoservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler
    public ApiResponse handle(Exception e) throws Exception{
        StackTraceElement[] stackTrace = e.getStackTrace();
        String className = "";
        try {
            className = stackTrace[0].getClassName();
        } catch (Exception classe) {
            log.error("无法获取抛出异常的类 " + classe.getMessage());
        }
        if(e instanceof  MyException) {
            return new ApiResponse(ApiResponse.FAIL, ((MyException) e).getMsg(), null);
        } else {
            log.error("系统抛出运行时异常", e);
            throw e;
        }
    }
}
