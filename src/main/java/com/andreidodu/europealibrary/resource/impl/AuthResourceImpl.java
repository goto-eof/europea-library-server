package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.resource.AuthResource;
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
public class AuthResourceImpl implements AuthResource {
    private final AuthenticationAndRegistrationService authenticationAndRegistrationService;

    @Override
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = this.authenticationAndRegistrationService.login(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @Override
    public ResponseEntity<UserDTO> getUser(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    UserDTO userDTO = this.authenticationAndRegistrationService.getMe(auth.getName());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.badRequest().build());

    }

    @Override
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.register(registrationRequestDTO));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    OperationStatusDTO operationStatusDTO = this.authenticationAndRegistrationService.changePassword(authentication.getName(), changePasswordRequestDTO);
                    return ResponseEntity.ok(operationStatusDTO);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<OperationStatusDTO> sendPasswordRecoveryEmail(@Valid @RequestBody PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.sendPasswordRecoveryEmail(passwordResetEmailRequestDTO.getEmail()));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> passwordReset(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.passwordReset(passwordResetRequestDTO));
    }
}
