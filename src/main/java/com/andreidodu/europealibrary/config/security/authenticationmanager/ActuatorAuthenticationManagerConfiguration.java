package com.andreidodu.europealibrary.config.security.authenticationmanager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service(value = "actuatorAuthenticationManager")
public class ActuatorAuthenticationManagerConfiguration implements AuthenticationManager {

    @Autowired
    @Qualifier(value = "actuatorUserDetailsService")
    private UserDetailsService actuatorUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        UserDetails user;

        try {
            user = actuatorUserDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("User does not exists");
        }

        if (StringUtils.isBlank(password) || !password.equals(user.getPassword())) {
            throw new BadCredentialsException("Password is wrong");
        }

        return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
    }
}
