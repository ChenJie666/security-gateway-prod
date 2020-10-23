//package com.iotmars.config;
//
//import com.iotmars.utils.Result;
//import com.netflix.client.ClientException;
//import feign.FeignException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * @author Administrator
// */
//@Slf4j
//@RestControllerAdvice
//public class ExceptionHandlerAdvice {
//
//    @ExceptionHandler({FeignException.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Result feignException(FeignException feignException) {
//        int status = feignException.status();
//        if (status >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
//            log.error("feignClient调用异常", feignException);
//        }
//        String msg = feignException.getMessage();
//
//        return Result.error(status,msg);
//    }
//
//    @ExceptionHandler({ IllegalArgumentException.class })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Result badRequestException(IllegalArgumentException exception) {
//
//        return Result.error(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
//    }
//
//    @ExceptionHandler({ClientException.class, Throwable.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Result serverException(Throwable throwable) {
//        log.error("服务端异常", throwable);
//
//        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"服务端异常，请联系管理员");
//    }
//
//}
//
