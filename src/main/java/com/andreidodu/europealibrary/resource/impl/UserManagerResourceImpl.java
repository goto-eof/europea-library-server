package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.UserEnableDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import com.andreidodu.europealibrary.resource.UserManagerResource;
import com.andreidodu.europealibrary.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserManagerResourceImpl implements UserManagerResource {

    private final UserManagerService userManagerService;

    @Override
    public ResponseEntity<UserDTO> getById(Long id) {
        return ResponseEntity.ok(this.userManagerService.getById(id));
    }

    @Override
    public ResponseEntity<CommonGenericCursoredResponseDTO<UserDTO>> getAllPaginated(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(this.userManagerService.getAll(commonCursoredRequestDTO));
    }

    @Override
    public ResponseEntity<UserDTO> enable(Long id, UserEnableDTO userEnableDTO) {
        return ResponseEntity.ok(this.userManagerService.enable(id, userEnableDTO.getEnabled()));
    }
}
