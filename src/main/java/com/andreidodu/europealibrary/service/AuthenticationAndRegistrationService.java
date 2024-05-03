package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.auth.*;
import org.springframework.http.ProblemDetail;

public interface AuthenticationAndRegistrationService {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);

    UserDTO getMe(String username);

    AuthResponseDTO register(RegistrationRequestDTO registrationRequestDTO);

    OperationStatusDTO changePassword(String name, ChangePasswordRequestDTO changePasswordRequestDTO);

    OperationStatusDTO sendPasswordRecoveryEmail(String email);

    OperationStatusDTO recoveryChangePassword(String name, RecoveryChangePasswordRequestDTO recoveryChangePasswordRequestDTO);
}
