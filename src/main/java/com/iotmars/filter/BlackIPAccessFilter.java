package com.iotmars.filter;

import com.iotmars.feign.BackendClient;
import com.iotmars.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 黑名单IP拦截<br>
 * 黑名单ip变化不会太频繁，<br>
 * 考虑到性能，我们不实时掉接口从别的服务获取了，<br>
 * 而是定时把黑名单ip列表同步到网关层,
 */
@Component
public class BlackIPAccessFilter implements GlobalFilter,Ordered {

	/**
	 * 黑名单列表
	 */
	private Set<String> blackIPs = new HashSet<>();


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (blackIPs.isEmpty()) {
			return chain.filter(exchange);
		}
		ServerHttpRequest request = exchange.getRequest();
//		HttpHeaders headers = request.getHeaders();

		String ip = getIpAddress(request);

		if(blackIPs.contains(ip)) {
			ServerHttpResponse response = exchange.getResponse();

			Result result = Result.error(HttpStatus.FORBIDDEN.value(), "鉴权失败");
			byte[] bytes = result.toString().getBytes();
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


	@Autowired
	private BackendClient backendClient;

	/**
	 * 定时同步黑名单IP
	 */
	@Scheduled(cron = "${cron.black-ip}")
	public void syncBlackIPList() {
		try {
			Set<String> list = backendClient.findAllBlackIPs(Collections.emptyMap());
			blackIPs = list;
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * 获取请求的真实ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddress(ServerHttpRequest request) {
		String ip = request.getHeaders().getFirst("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddress().getAddress().getHostAddress();
		}
		return ip;
	}

}
