package com.iotmars.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.core.codec.Base64;
import com.iotmars.feign.ExpiredTokenFeign;
import com.iotmars.utils.CommonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.CharBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: CJ
 * @Data: 2020/9/30 14:00
 */
@Component
public class ExpiredTokenFilter implements GlobalFilter, Ordered {

    private Map<String, Long> expiredMap = Collections.emptyMap();

    @Resource
    private ExpiredTokenFeign expiredTokenFeign;

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    /**
     * 定时同步过期用户
     */
    @Scheduled(fixedDelay = 5000)
//    @Scheduled(cron = "${cron.sync_expired_token}")
//    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncBlackIPList() {
        try {
            expiredMap = expiredTokenFeign.getExpiredToken();
            System.out.println("同步过期token:" + expiredMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 筛选已失效的token
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (expiredMap.isEmpty()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();

        String token = "";
        //从请求头中获取token
        HttpHeaders httpHeaders = request.getHeaders();
        List<String> authorization = httpHeaders.get("Authorization");
        if (!Objects.isNull(authorization)) {
            for (String value : authorization) { // typically there is only one (most servers enforce that)
                if ((value.toLowerCase().startsWith("Bearer".toLowerCase()))) {
                    token = value.substring("Bearer".length()).trim();
                    // Add this here for the auth details later. Would be better to change the signature of this method.
                    int commaIndex = token.indexOf(',');
                    if (commaIndex > 0) {
                        token = token.substring(0, commaIndex);
                    }
                }
            }
        }

        //从参数中获取token
        if (StringUtils.isEmpty(token)) {
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            token = queryParams.getFirst("access_token");
            if (StringUtils.isEmpty(queryParams.getFirst("grant_type"))) {
                token = queryParams.getFirst("refresh_token");
            }
        }

        if (!StringUtils.isEmpty(token)) {
            int firstPeriod = token.indexOf('.');
            int lastPeriod = token.lastIndexOf('.');

            if (firstPeriod <= 0 || lastPeriod <= firstPeriod) {
                throw new IllegalArgumentException("JWT must have 3 tokens");
            }
            CharBuffer buffer = CharBuffer.wrap(token, 0, firstPeriod);

            buffer.limit(lastPeriod).position(firstPeriod + 1);
            byte[] decode = Base64.decode(buffer);
            String content = new String(decode);
            JSONObject jsonObject = new JSONObject(content);
            String userId = jsonObject.getStr("user_name");
            Long createTimestamp = jsonObject.getLong("cre");

            boolean isExpired = expiredMap.containsKey(userId) && expiredMap.get(userId).compareTo(createTimestamp) > 0;
//            Assert.isTrue(!isExpired, "gateway: 令牌已失效，请重新登录");
            if (isExpired) {
                ServerHttpResponse response = exchange.getResponse();

                CommonResult<Object> error = CommonResult.error(HttpStatus.UNAUTHORIZED.value(), "gateway: token已失效，请重新登录");
                byte[] bytes = error.toString().getBytes();
                DataBuffer wrap = response.bufferFactory().wrap(bytes);

                response.setStatusCode(HttpStatus.OK);
                return response.writeWith(Mono.just(wrap));
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
