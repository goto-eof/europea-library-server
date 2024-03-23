package com.andreidodu.europealibrary.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("batch-db")
public class BatchDbConfig {
    private String url;
    private String username;
    private String password;
}



