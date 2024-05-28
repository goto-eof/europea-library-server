package com.andreidodu.europealibrary.config.security.userdetailsservice;


import com.andreidodu.europealibrary.constants.AuthConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class ActuatorUserDetailsServiceConfiguration {

    @Value("${com.andreidodu.europea-library.actuator.security.username}")
    private String actuatorUsername;

    @Value("${com.andreidodu.europea-library.actuator.security.password}")
    private String actuatorPassword;

    @Bean
    public UserDetailsService actuatorUserDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(this.actuatorUsername)
                .password(this.actuatorPassword)
                .authorities(AuthConst.AUTHORITY_ACTUATOR)
                .build());
        return manager;
    }

}


