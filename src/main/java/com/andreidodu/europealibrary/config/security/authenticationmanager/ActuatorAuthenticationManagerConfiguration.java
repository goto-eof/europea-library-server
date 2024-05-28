package com.andreidodu.europealibrary.config.security.authenticationmanager;


import com.andreidodu.europealibrary.constants.AuthConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class ActuatorAuthenticationManagerConfiguration {

    @Value("${com.andreidodu.europea-library.actuator.security.username}")
    private String actuatorUsername;

    @Value("${com.andreidodu.europea-library.actuator.security.password}")
    private String actuatorPassword;

    public UserDetailsService actuatorUserDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(this.actuatorUsername)
                .password("{noop}" + this.actuatorPassword)
                .authorities(AuthConst.AUTHORITY_ACTUATOR)
                .build());
        return manager;
    }

    @Bean(value = "actuatorAuthenticationManager")
    public AuthenticationManager actuatorAuthenticationManager() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(actuatorUserDetailsService());
        return new ProviderManager(authProvider);
    }
}