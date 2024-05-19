package com.andreidodu.europealibrary.service.impl.security;

import com.andreidodu.europealibrary.constants.AuthConst;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.PasswordResetEmailRequestDTO;
import com.andreidodu.europealibrary.dto.security.*;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.AuthorityMapper;
import com.andreidodu.europealibrary.model.security.Authority;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.security.UserRepository;
import com.andreidodu.europealibrary.service.AuthenticationAndRegistrationService;
import com.andreidodu.europealibrary.service.EmailSenderService;
import com.andreidodu.europealibrary.service.TemplateService;
import com.andreidodu.europealibrary.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationAndRegistrationServiceImpl implements AuthenticationAndRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationAndRegistrationServiceImpl.class);
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthorityMapper authorityMapper;
    private final EmailSenderService emailSenderService;
    private final TemplateService templateService;

    @Value("${com.andreidodu.europea-library.password.reset.minutes-to-wait-for-another-attempt}")
    private int timeToWaitForAnotherAttempt;

    @Value("${com.andreidodu.europea-library.password.reset.mail.from}")
    private String mailFrom;

    @Value("${com.andreidodu.europea-library.password.reset.mail.title}")
    private String passwordResetMailTitle;

    @Value("${com.andreidodu.europea-library.client.url}")
    private String clientUrl;

    @Value("${com.andreidodu.europea-library.client.reset-password-endpoint}")
    private String clientResetPasswordEndpoint;

    @Value("${com.andreidodu.europea-library.password.reset.token.ttl.minutes}")
    private int passwordResetTokenTtlMinutes;


    @Override
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

    @Override
    public UserDTO getMe(String username) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setAuthorityList(this.authorityMapper.toDTO(user.getAuthorityList()));
        return userDTO;
    }

    @Override
    public AuthResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {
        validateInput(registrationRequestDTO);
        validateUserAlreadyExists(registrationRequestDTO);

        User user = createUser(registrationRequestDTO);
        List<Authority> authorityList = buildAuthorityList(user);
        user.setAuthorityList(authorityList);

        User savedUser = this.userRepository.save(user);
        return this.login(new AuthRequestDTO(registrationRequestDTO.getUsername(), registrationRequestDTO.getPassword()));
    }

    @Override
    public OperationStatusDTO changePassword(String name, ChangePasswordRequestDTO changePasswordRequestDTO) {
        assertNotEmpty(changePasswordRequestDTO, "payload could not be empty");
        assertNotEmpty(changePasswordRequestDTO.getOldPassword(), "old password could not be empty");
        assertNotEmpty(changePasswordRequestDTO.getNewPassword(), "new password could not be empty");

        if (changePasswordRequestDTO.getOldPassword().equals(changePasswordRequestDTO.getNewPassword())) {
            return new OperationStatusDTO(false, "passwords matches");
        }

        Optional<User> userOptional = this.userRepository.findByUsername(name);

        if (userOptional.isEmpty()) {
            return new OperationStatusDTO(false, "username does not exists");
        }

        User user = userOptional.get();

        if (!this.passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            return new OperationStatusDTO(false, "invalid old password");
        }

        user.setPassword(this.passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
        this.userRepository.save(user);

        return new OperationStatusDTO(true, "password changed");
    }

    @Override
    public OperationStatusDTO sendPasswordResetEmail(String mailTo) {
        assertNotEmpty(mailTo, "email could not be empty");

        Optional<User> userOptional = this.userRepository.findByEmail(mailTo);
        User user = userOptional.orElseThrow(() -> new ValidationException("User does not exists"));

        LocalDateTime recoveryRequestLocalDateTime = user.getRecoveryRequestTimestamp();
        if (recoveryRequestLocalDateTime != null && ChronoUnit.MINUTES.between(recoveryRequestLocalDateTime, LocalDateTime.now()) < timeToWaitForAnotherAttempt) {
            return new OperationStatusDTO(false, "It is necessary to wait at least " + timeToWaitForAnotherAttempt + " minute/s");
        }

        String recoveryKey = generateRandomUUID();

        updateUsersResetToken(user, recoveryKey);

        PasswordResetEmailRequestDTO passwordResetEmailRequestDTO = new PasswordResetEmailRequestDTO();
        passwordResetEmailRequestDTO.setUsername(user.getUsername());
        passwordResetEmailRequestDTO.setRedirectTo(clientUrl + clientResetPasswordEndpoint + "/" + recoveryKey);
        try {
            String emailContent = this.templateService.generatePasswordResetEmailContent(passwordResetEmailRequestDTO);
            this.emailSenderService.sendEmail(passwordResetMailTitle, mailFrom, mailTo, emailContent);
            return new OperationStatusDTO(true, "email sent");
        } catch (Exception e) {
            return new OperationStatusDTO(false, "email not sent");
        }
    }

    private static String generateRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void updateUsersResetToken(User user, String recoveryKey) {
        LocalDateTime now = LocalDateTime.now();
        user.setResetToken(recoveryKey);
        user.setRecoveryRequestTimestamp(now);
        userRepository.save(user);
    }

    @Override
    public OperationStatusDTO passwordReset(PasswordResetRequestDTO passwordResetRequestDTO) {
        assertNotEmpty(passwordResetRequestDTO, "payload could not be empty");
        assertNotEmpty(passwordResetRequestDTO.getResetToken(), "token could not be empty");
        assertNotEmpty(passwordResetRequestDTO.getPassword(), "password could not be empty");

        User user = this.userRepository.findByResetToken(passwordResetRequestDTO.getResetToken())
                .orElseThrow(() -> new ValidationException("token does not exists"));

        LocalDateTime recoveryRequestLocalDateTime = user.getRecoveryRequestTimestamp();
        if (recoveryRequestLocalDateTime != null && ChronoUnit.MINUTES.between(recoveryRequestLocalDateTime, LocalDateTime.now()) > passwordResetTokenTtlMinutes) {
            return new OperationStatusDTO(false, "Reset token expired");
        }

        user.setPassword(this.passwordEncoder.encode(passwordResetRequestDTO.getPassword()));
        user.setResetToken(null);
        this.userRepository.save(user);

        return new OperationStatusDTO(true, "password updated successfully");
    }

    private void validateUserAlreadyExists(RegistrationRequestDTO registrationRequestDTO) {
        if (this.userRepository.existsByUsername(registrationRequestDTO.getUsername())) {
            throw new ValidationException("username not available");
        }
        if (this.userRepository.existsByEmail(registrationRequestDTO.getEmail())) {
            throw new ValidationException("email already used");
        }

    }

    private static List<Authority> buildAuthorityList(User user) {
        List<Authority> authorityList = new ArrayList<>();
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthConst.AUTHORITY_USER);
        userAuthority.setUser(user);
        authorityList.add(userAuthority);
        return authorityList;
    }

    private User createUser(RegistrationRequestDTO registrationRequestDTO) {
        User user = new User();
        user.setUsername(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getUsername()));
        user.setEmail(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getEmail()));
        user.setPassword(this.passwordEncoder.encode(registrationRequestDTO.getPassword()));
        user.setEnabled(true);
        return user;
    }

    private static void validateInput(RegistrationRequestDTO registrationRequestDTO) {
        assertNotEmpty(registrationRequestDTO, "payload could not be empty");
        assertNotEmpty(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getUsername()), "username could not be empty");
        assertNotEmpty(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getEmail()), "email could not be empty");
        assertNotEmpty(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getPassword()), "password could not be empty");
    }


    public static <T> void assertNotEmpty(T t, String message) {
        if (t == null) {
            throw new ValidationException(message);
        }
    }

    public static <T extends String> void assertNotEmpty(T t, String message) {
        if (t == null || t.trim().equalsIgnoreCase("")) {
            throw new ValidationException(message);
        }
    }
}