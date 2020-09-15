package com.iotmars.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * spring security配置
 */
//@EnableOAuth2Sso
@EnableWebFluxSecurity
public class SecurityConfig {


	//security的鉴权排除的url列表
	private static final String[] excludedAuthPages = {"/v1/api-menu/menu/**","/v1/api-menu/menu-anon/**"};

	@Bean
	SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {
//		http.authorizeExchange()
//				.pathMatchers(excludedAuthPages).permitAll()  //无需进行权限过滤的请求路径
//				.pathMatchers(HttpMethod.OPTIONS).permitAll() //option 请求默认放行
//				.anyExchange().authenticated()
//				.and()
//				.httpBasic()
//				.and()
//				.formLogin() //启动页面表单登陆,spring security 内置了一个登陆页面/login
//				.and().csrf().disable()//必须支持跨域
//				.logout().disable();
		http.csrf().disable()
				.authorizeExchange()
				.pathMatchers(excludedAuthPages).permitAll()
				.anyExchange().authenticated();

//		http.oauth2ResourceServer().jwt();

		return http.build();
	}
}