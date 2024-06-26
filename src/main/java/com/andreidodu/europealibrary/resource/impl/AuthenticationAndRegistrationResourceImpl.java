package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.security.*;
import com.andreidodu.europealibrary.resource.AuthenticationAndRegistrationResource;
import com.andreidodu.europealibrary.service.AuthenticationAndRegistrationService;
import com.andreidodu.europealibrary.service.GoogleReCaptchaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationAndRegistrationResourceImpl implements AuthenticationAndRegistrationResource, com.andreidodu.europealibrary.constants.CookieConst {

    @Value("${com.andreidodu.europea-library.session.ttl-seconds}")
    private int sessionTTLSeconds;

    private final GoogleReCaptchaService googleReCaptchaService;

    private final AuthenticationAndRegistrationService authenticationAndRegistrationService;

    @Override
    public ResponseEntity<AuthResponseDTO> login(HttpServletRequest request, HttpServletResponse response, AuthRequestDTO authRequestDTO) {
        this.googleReCaptchaService.verify(request.getRemoteAddr(), authRequestDTO.getClientCaptchaToken());

        AuthResponseDTO authResponseDTO = this.authenticationAndRegistrationService.login(authRequestDTO);
        Cookie deviceIdentifierCookie = createDeviceIdentifierCookie(authResponseDTO.getAgentId());
        response.addCookie(deviceIdentifierCookie);
        return new ResponseEntity<AuthResponseDTO>(authResponseDTO, HttpStatus.OK);
    }

    private Cookie createDeviceIdentifierCookie(String agentId) {
        Cookie deviceIdCookie = new Cookie(COOKIE_NAME_DEVICE_ID, agentId);
        deviceIdCookie.setPath("/");
        deviceIdCookie.setHttpOnly(true);
        deviceIdCookie.setAttribute("SameSite", "strict");
        // TODO configure the application in order to work also with HTTPS
        // deviceIdCookie.setSecure(true);
        deviceIdCookie.setMaxAge(sessionTTLSeconds);
        return deviceIdCookie;
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
    public ResponseEntity<AuthResponseDTO> register(HttpServletRequest request, HttpServletResponse response, RegistrationRequestDTO registrationRequestDTO) {
        this.googleReCaptchaService.verify(request.getRemoteAddr(), registrationRequestDTO.getClientCaptchaToken());

        AuthResponseDTO authResponseDTO = this.authenticationAndRegistrationService.register(registrationRequestDTO);
        Cookie deviceIdentifierCookie = createDeviceIdentifierCookie(authResponseDTO.getAgentId());
        response.addCookie(deviceIdentifierCookie);
        return new ResponseEntity<AuthResponseDTO>(authResponseDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OperationStatusDTO> changePassword(ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    OperationStatusDTO operationStatusDTO = this.authenticationAndRegistrationService.changePassword(authentication.getName(), changePasswordRequestDTO);
                    return ResponseEntity.ok(operationStatusDTO);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<OperationStatusDTO> sendPasswordRecoveryEmail(PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.sendPasswordResetEmail(passwordResetEmailRequestDTO.getEmail()));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> passwordReset(PasswordResetRequestDTO passwordResetRequestDTO) {
        return ResponseEntity.ok(this.authenticationAndRegistrationService.passwordReset(passwordResetRequestDTO));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> logout(String deviceId, HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        return ResponseEntity.ok(this.authenticationAndRegistrationService.logout(token, deviceId));
    }
}
