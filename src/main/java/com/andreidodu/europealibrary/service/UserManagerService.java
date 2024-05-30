package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;

public interface UserManagerService {
    CursorDTO<UserDTO> getAll(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
