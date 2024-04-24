package com.andreidodu.europealibrary;

import com.andreidodu.europealibrary.config.auth.RsaKeyConfigProperties;
import com.andreidodu.europealibrary.util.PostApplicationRunUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@RequiredArgsConstructor
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class EuropeaLibraryApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(EuropeaLibraryApplication.class, args);
        context.getBean(PostApplicationRunUtil.class).performActions();
    }


}
