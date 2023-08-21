package com.jwyao.system.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     * 跨域请求配置
     * @return
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");                                                 // 设置访问源地址
        corsConfiguration.addAllowedHeader("*");                                                 // 设置访问源请求头
        corsConfiguration.addAllowedMethod("*");                                                 // 设置访问源请求方法
        corsConfiguration.setAllowCredentials(true);                                             // 设置携带凭证cookie
        corsConfiguration.setMaxAge(3600L);                                                      // 设置跨域请求最大有效时长
        source.registerCorsConfiguration("/**", corsConfiguration);                         // 对接口配置跨域设置
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);                                                // 设置优先级最高
        return bean;
    }

}
