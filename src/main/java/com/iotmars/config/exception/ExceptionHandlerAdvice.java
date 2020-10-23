package com.iotmars.config.exception;

import com.iotmars.utils.CommonResult;
import com.netflix.client.ClientException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author Administrator
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({FeignException.class})
    @ResponseStatus(HttpStatus.OK)
    public CommonResult<String> feignException(FeignException feignException) {
        int status = feignException.status();
        if (status >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error("feignClient调用异常", feignException);
        }
        String msg = feignException.getMessage();

        return CommonResult.error(status,msg);
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.OK)
    public CommonResult<String> badRequestException(IllegalArgumentException exception) {
        return CommonResult.error(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler({ClientException.class, Throwable.class})
    @ResponseStatus(HttpStatus.OK)
    public CommonResult<String> serverException(Throwable throwable) {
        log.error("服务端异常", throwable);

        return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"服务端异常，请联系管理员");
    }


    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public CommonResult<String> exception(Exception exception){
        exception.printStackTrace();
        String msg = exception.getMessage();
        return CommonResult.error(500,msg);
    }

}

