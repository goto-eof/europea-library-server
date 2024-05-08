package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/settings")
public interface ApplicationSettingsResource {

    @GetMapping
    ResponseEntity<ApplicationSettingsDTO> get();

    @PatchMapping
    @AllowOnlyAdministrator
    ResponseEntity<ApplicationSettingsDTO> update(@Valid @RequestBody ApplicationSettingsDTO applicationSettingsDTO);

}
