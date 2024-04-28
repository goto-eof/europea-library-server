package com.andreidodu.europealibrary.service.impl.auth;


import com.andreidodu.europealibrary.mapper.AuthUserMapper;
import com.andreidodu.europealibrary.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthUserMapper authUserMapper;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(authUserMapper::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found: " + username));
    }
}
