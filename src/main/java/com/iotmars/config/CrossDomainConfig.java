package com.iotmars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 跨域配置<br>
 * 页面访问域名和后端接口地址的域名不一致时，会先发起一个OPTIONS的试探请求<br>
 * 如果不设置跨域的话，js将无法正确访问接口，域名一致的话，不存在这个问题
 */
@Configuration
public class CrossDomainConfig {

    /**
     * 跨域支持
     *
     * @return
     */
    @Bean
    public CorsWebFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许cookies跨域
        config.addAllowedOrigin("*");// #允许向该服务器提交请求的URI，*表示全部允许
        config.addAllowedHeader("*");// #允许访问的头信息,*表示全部
        config.setMaxAge(18000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.addAllowedMethod("*");// 允许提交请求的方法，*表示全部允许
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    //方式二
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // 拦截所有权请求
//                        .allowCredentials(true) // 允许cookies跨域
//                        .allowedOrigins("*") // #允许向该服务器提交请求的URI，*表示全部允许
//                        .allowedHeaders("*") // #允许访问的头信息,*表示全部
//                        .maxAge(18000L); // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
//                        .allowedMethods("*") // 允许提交请求的方法，*表示全部允许
//            }
//        };
//    }

    //方式三：通过配置文件配置
    /**
     *     spring:
     *     cloud:
     *     gateway:
     *     globalcors:
     *     corsConfigurations:
     *             '[/**]':
     *     allowedOrigins: "*"
     *     allowedMethods: "*"
     */

}
