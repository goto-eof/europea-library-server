package com.andreidodu.europealibrary.service.auth;


import com.andreidodu.europealibrary.mapper.AuthUserMapper;
import com.andreidodu.europealibrary.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthUserMapper authUserMapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(authUserMapper::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found: " + username));
    }
}
