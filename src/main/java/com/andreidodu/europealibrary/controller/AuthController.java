package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.auth.AuthRequestDTO;
import com.andreidodu.europealibrary.dto.auth.AuthResponseDTO;
import com.andreidodu.europealibrary.dto.auth.RegistrationRequestDTO;
import com.andreidodu.europealibrary.dto.auth.UserDTO;
import com.andreidodu.europealibrary.service.auth.AuthenticationAndRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationAndRegistrationService authenticationAndRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = this.authenticationAndRegistrationService.login(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    UserDTO userDTO = this.authenticationAndRegistrationService.getUser(auth.getName());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.badRequest().build());

    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.register(registrationRequestDTO));
    }
}
