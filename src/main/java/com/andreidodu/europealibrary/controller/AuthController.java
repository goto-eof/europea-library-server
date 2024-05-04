package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.auth.*;
import com.andreidodu.europealibrary.service.AuthenticationAndRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationAndRegistrationService authenticationAndRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = this.authenticationAndRegistrationService.login(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    UserDTO userDTO = this.authenticationAndRegistrationService.getMe(auth.getName());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.badRequest().build());

    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.register(registrationRequestDTO));
    }

    @PostMapping("/password/change")
    public ResponseEntity<OperationStatusDTO> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.changePassword(authentication.getName(), changePasswordRequestDTO));
    }

    @PostMapping("/password/reset/sendEmail")
    public ResponseEntity<OperationStatusDTO> sendPasswordRecoveryEmail(@Valid @RequestBody PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.sendPasswordRecoveryEmail(passwordResetEmailRequestDTO.getEmail()));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<OperationStatusDTO> passwordReset(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.passwordReset(passwordResetRequestDTO));
    }
}
