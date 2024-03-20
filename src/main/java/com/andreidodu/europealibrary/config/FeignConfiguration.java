package com.andreidodu.europealibrary.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFeignClients(basePackages = {"com.andreidodu.europealibrary.client"})
@Import({FeignClientsConfiguration.class})
public class FeignConfiguration {

}
