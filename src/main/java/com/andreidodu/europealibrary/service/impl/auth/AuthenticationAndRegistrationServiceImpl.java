package com.andreidodu.europealibrary.service.impl.auth;

import com.andreidodu.europealibrary.constants.AuthConst;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.auth.*;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.AuthorityMapper;
import com.andreidodu.europealibrary.mapper.UserMapper;
import com.andreidodu.europealibrary.model.auth.Authority;
import com.andreidodu.europealibrary.model.auth.User;
import com.andreidodu.europealibrary.repository.auth.UserRepository;
import com.andreidodu.europealibrary.service.AuthenticationAndRegistrationService;
import com.andreidodu.europealibrary.service.EmailSenderService;
import com.andreidodu.europealibrary.util.StringUtil;
import com.mysema.commons.lang.Assert;
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
    private final UserMapper userMapper;
    private final AuthorityMapper authorityMapper;
    private final EmailSenderService emailSenderService;

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
        Assert.notNull(changePasswordRequestDTO, "payload could not be empty");
        Assert.notNull(changePasswordRequestDTO.getOldPassword(), "old password could not be empty");
        Assert.notNull(changePasswordRequestDTO.getNewPassword(), "new password could not be empty");

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
    public OperationStatusDTO sendPasswordRecoveryEmail(String email) {
        Assert.notNull(email, "email could not be empty");

        Optional<User> userOptional = this.userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new ValidationException("User does not exists"));

        LocalDateTime recoveryRequestLocalDateTime = user.getRecoveryRequestTimestamp();
        if (recoveryRequestLocalDateTime != null && ChronoUnit.MINUTES.between(recoveryRequestLocalDateTime, LocalDateTime.now()) < 1) {
            return new OperationStatusDTO(false, "It is necessary to wait at least 1 minute");
        }

        String recoveryKey = generateRandomUUID();

        updateUsersRecoveryKey(user, recoveryKey);

        this.emailSenderService.sendPasswordRecoveryEmail("Europea Library Password Recovery Link", "andrei@andrei.it", email, "Hello, this is your one-shot key: " + recoveryKey);

        return new OperationStatusDTO(true, "email sent");
    }

    private static String generateRandomUUID() {
        UUID uuid = UUID.randomUUID();
        String recoveryKey = uuid.toString();
        return recoveryKey;
    }

    private void updateUsersRecoveryKey(User user, String recoveryKey) {
        LocalDateTime now = LocalDateTime.now();
        user.setRecoveryKey(recoveryKey);
        user.setRecoveryRequestTimestamp(now);
        user.setRecoveryExpirationTimestamp(now.minusMinutes(1));
        userRepository.save(user);
    }

    @Override
    public OperationStatusDTO recoveryChangePassword(String name, RecoveryChangePasswordRequestDTO recoveryChangePasswordRequestDTO) {
        return null;
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
        Assert.notNull(registrationRequestDTO, "payload could not be empty");
        Assert.notNull(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getUsername()), "username could not be empty");
        Assert.notNull(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getEmail()), "email could not be empty");
        Assert.notNull(StringUtil.cleanAndTrimToNull(registrationRequestDTO.getPassword()), "password could not be empty");
    }
}