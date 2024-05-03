package com.andreidodu.europealibrary.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "com.andreidodu.europea-library.mail")
public class EmailConfigurationProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean authEnabled;
    private boolean startTlsEnabled;
}
