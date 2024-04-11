package com.andreidodu.europealibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class EuropeaLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuropeaLibraryApplication.class, args);
    }

}
