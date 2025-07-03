package com.diary.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /temp-uploads/** 요청을 실제 파일 시스템 경로와 연결
        registry.addResourceHandler("/temp-uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/temp-uploads/");
        
        // 정식 업로드 이미지
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
        
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
