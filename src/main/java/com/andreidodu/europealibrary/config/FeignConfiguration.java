package com.andreidodu.europealibrary.config;

import com.andreidodu.europealibrary.constants.ClientConst;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFeignClients(basePackages = {ClientConst.FEIGN_CLIENTS_PACKAGE})
@Import({FeignClientsConfiguration.class})
public class FeignConfiguration {

}
