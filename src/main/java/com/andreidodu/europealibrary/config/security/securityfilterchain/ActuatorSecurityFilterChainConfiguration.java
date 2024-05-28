package com.andreidodu.europealibrary.config.security.securityfilterchain;

import com.andreidodu.europealibrary.constants.AuthConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ActuatorSecurityFilterChainConfiguration {

    @Autowired
    @Qualifier(value = "actuatorAuthenticationManager")
    private AuthenticationManager actuatorAuthenticationManager;

    @Bean
    @Order(2)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        Customizer<HttpBasicConfigurer<HttpSecurity>> httpBasic = Customizer.withDefaults();

        httpSecurity
                .authenticationManager(actuatorAuthenticationManager)
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/actuator/**")
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((auth) -> {
                    auth.requestMatchers("/actuator/**")
                            .hasAuthority(AuthConst.AUTHORITY_ACTUATOR);
                    auth.anyRequest().permitAll();
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(httpBasic);
        return httpSecurity.build();
    }
}
