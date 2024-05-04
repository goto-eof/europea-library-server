package com.andreidodu.europealibrary.config;


import com.andreidodu.europealibrary.constants.PersistenceConst;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(PersistenceConst.BATCH_DB_CONFIGURATION_PREFIX)
public class BatchDbConfig {
    private String url;
    private String username;
    private String password;
}



