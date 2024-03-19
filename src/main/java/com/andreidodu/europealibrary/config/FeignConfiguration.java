package com.andreidodu.europealibrary.config;

import com.andreidodu.europealibrary.client.OpenLibraryClient;
import com.andreidodu.europealibrary.client.OpenLibraryDecoder;
import feign.Feign;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFeignClients(basePackages = {"com.andreidodu.europealibrary.client"})
@Import({FeignClientsConfiguration.class})
public class FeignConfiguration {

    @Autowired
    private OpenLibraryDecoder openLibraryDecoder;
    @Autowired
    Encoder encoder;

    @Bean("openLibraryClient")
    public OpenLibraryClient openLibraryClient() {
        return Feign.builder()
                .encoder(encoder)
                .decoder(openLibraryDecoder)
                .target(OpenLibraryClient.class, "https://openlibrary.org");
    }

}
