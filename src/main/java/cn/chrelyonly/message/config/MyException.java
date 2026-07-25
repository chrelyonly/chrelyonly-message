package cn.chrelyonly.message.config;

import cn.chrelyonly.message.component.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author chrelyonly
 * 重写异常处理
 */
@RestControllerAdvice
@Slf4j
public class MyException{

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> method(HttpRequestMethodNotSupportedException e) {

        log.error("", e);

        return R.fail("请求方式错误喵~");
    }

    @ExceptionHandler(Exception.class)
    public R<?> exception(Exception e) {

        log.error("", e);

        return R.fail("系统异常喵,要看日志的喵~");
    }


}
