package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.security.*;

public interface AuthenticationAndRegistrationService {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);

    UserDTO getMe(String username);

    AuthResponseDTO register(RegistrationRequestDTO registrationRequestDTO);

    OperationStatusDTO changePassword(String name, ChangePasswordRequestDTO changePasswordRequestDTO);

    OperationStatusDTO sendPasswordResetEmail(String email);

    OperationStatusDTO passwordReset(PasswordResetRequestDTO passwordResetRequestDTO);

    OperationStatusDTO logout(String token, String agentId);
}
