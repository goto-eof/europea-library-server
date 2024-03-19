package com.andreidodu.europealibrary.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("open-library.authentication")
public class OpenLibraryConfiguration {
    private String access;
    private String secret;
}
