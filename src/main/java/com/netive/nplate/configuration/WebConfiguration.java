package com.netive.nplate.configuration;

import com.netive.nplate.interceptor.LoggerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

//    private String connectPath = "/nplateImage/**";
//    private String resourcePath = "file:////home/ec2-user/nplateImage/";
    private String connectPath = "/nplateImage/**";
    private String resourcePath = "file:///D:/home/ec2-user/nplateImage/";

    // 외부 폴더 경로 요청들어올 경우 (이미지)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(connectPath).addResourceLocations(resourcePath);
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");  // 파일 인코딩 설정
        multipartResolver.setMaxUploadSize(10 * 1024 * 1024);
        return multipartResolver;
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoggerInterceptor()).excludePathPatterns("/parsley/**", "/smarteditor/**", "/startbootstrap-sb-admin-2-gh-pages/**", "/startbootstrap-shop-homepage-gh-pages/**");
//    }
}
