package com.iotmars.controller;

import com.iotmars.utils.CommonResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HystrixController {

    @RequestMapping("/fallback")
    public CommonResult fallBack(){
        return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"网关调用服务异常");
    }

}
