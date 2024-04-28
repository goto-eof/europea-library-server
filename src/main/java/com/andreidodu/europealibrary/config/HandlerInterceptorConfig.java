package com.andreidodu.europealibrary.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class HandlerInterceptorConfig implements WebMvcConfigurer {
    private final AnyHandlerInterceptor anyHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(anyHandlerInterceptor);
    }
}