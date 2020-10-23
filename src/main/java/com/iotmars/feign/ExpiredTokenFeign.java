package com.iotmars.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @Description:
 * @Author: CJ
 * @Data: 2020/9/30 13:43
 */
@FeignClient(name = "TEST-USER-CENTER")
public interface ExpiredTokenFeign {

    @GetMapping(path = "/feign/user/getExpiredToken")
    Map<String,Long> getExpiredToken();

}
