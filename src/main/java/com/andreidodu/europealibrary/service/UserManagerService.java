package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;

public interface UserManagerService {
    CommonGenericCursoredResponseDTO<UserDTO> getAll(CommonCursoredRequestDTO commonCursoredRequestDTO);

    UserDTO getById(Long id);

    UserDTO enable(Long id, boolean isEnabled);
}
