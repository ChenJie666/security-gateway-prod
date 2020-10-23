package com.iotmars.filter;

import com.iotmars.utils.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 过滤uri<br>
 * 该类uri不需要登陆，但又不允许外网通过网关调用，只允许微服务间在内网调用，<br>
 * 为了方便拦截此场景的uri，我们自己约定一个规范，及uri中含有-anon/internal<br>
 * 如在oauth登陆的时候用到根据username查询用户，<br>
 * 用户系统提供的查询接口/users-anon/internal肯定不能做登录拦截，而该接口也不能对外网暴露<br>
 * 如果有此类场景的uri，请用这种命名格式
 */
@Component
@Slf4j
public class InternalURIAccessFilter implements GlobalFilter,Ordered {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		RequestPath path = request.getPath();
		String uri = path.contextPath().value();
		log.info("request.getPath():" + path);

		if (PatternMatchUtils.simpleMatch("/feign/**", uri)) {
			ServerHttpResponse response = exchange.getResponse();

			CommonResult<String> commonResult = CommonResult.error(HttpStatus.FORBIDDEN.value(), "访问拒绝");
			byte[] bytes = commonResult.toString().getBytes();
			DataBuffer buffer = response.bufferFactory().wrap(bytes);

			response.setStatusCode(HttpStatus.FORBIDDEN);
			return response.writeWith(Mono.just(buffer));
		}

		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
