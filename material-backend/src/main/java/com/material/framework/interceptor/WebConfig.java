package com.material.framework.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置 - 注册拦截器与静态资源
 */
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Autowired
    private JwtInterceptor jwtInterceptor;

    /** 上传文件路径 */
    private static final String PROFILE_PATH = "file:D:/material/uploadPath/";

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/captchaImage",
                        "/profile/**",
                        "/common/download/**",
                        "/static/**",
                        "/webjars/**",
                        "/swagger-resources/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // 上传文件映射
        registry.addResourceHandler("/profile/**")
                .addResourceLocations(PROFILE_PATH);
    }
}
