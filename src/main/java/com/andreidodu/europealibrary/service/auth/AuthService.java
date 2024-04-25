package com.andreidodu.europealibrary.service.auth;

import com.andreidodu.europealibrary.dto.auth.AuthRequestDTO;
import com.andreidodu.europealibrary.dto.auth.AuthResponseDTO;
import com.andreidodu.europealibrary.dto.auth.AuthUserDTO;
import com.andreidodu.europealibrary.dto.auth.UserDTO;
import com.andreidodu.europealibrary.model.auth.User;
import com.andreidodu.europealibrary.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                authRequestDTO.getUsername(),
                                authRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthUserDTO userDetails = (AuthUserDTO) authentication.getPrincipal();

        log.info("Token requested for user: {}", authentication.getAuthorities());
        String token = this.generateToken(authentication);

        return generateAuthResponse(token);
    }

    private static AuthResponseDTO generateAuthResponse(String token) {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setToken(token);
        return authResponseDTO;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    public UserDTO getUser(String username) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }
}