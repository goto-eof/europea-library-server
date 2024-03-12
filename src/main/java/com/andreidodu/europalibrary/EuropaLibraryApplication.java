package com.andreidodu.europalibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication( exclude = HibernateJpaAutoConfiguration.class)
public class EuropaLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(EuropaLibraryApplication.class, args);
	}

}
