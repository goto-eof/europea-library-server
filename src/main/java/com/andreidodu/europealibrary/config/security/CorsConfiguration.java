package com.andreidodu.europealibrary.config.security;

import feign.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfiguration {

    @Value("${com.andreidodu.europea-library.client.url}")
    private String allowedOrigin;

    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOriginPatterns(List.of(allowedOrigin));
        corsConfiguration.setAllowedMethods(Arrays.asList(
                Request.HttpMethod.GET.name(),
                Request.HttpMethod.HEAD.name(),
                Request.HttpMethod.POST.name(),
                Request.HttpMethod.PUT.name(),
                Request.HttpMethod.PATCH.name(),
                Request.HttpMethod.DELETE.name()));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setMaxAge(1800L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
