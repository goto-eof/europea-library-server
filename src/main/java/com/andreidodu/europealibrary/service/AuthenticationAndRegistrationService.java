package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.auth.AuthRequestDTO;
import com.andreidodu.europealibrary.dto.auth.AuthResponseDTO;
import com.andreidodu.europealibrary.dto.auth.RegistrationRequestDTO;
import com.andreidodu.europealibrary.dto.auth.UserDTO;

public interface AuthenticationAndRegistrationService {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);

    UserDTO getMe(String username);

    AuthResponseDTO register(RegistrationRequestDTO registrationRequestDTO);
}
