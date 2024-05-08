package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.resource.ApplicationSettingsResource;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApplicationSettingsResourceImpl implements ApplicationSettingsResource {
    private final ApplicationSettingsService applicationSettingsService;

    @Override
    public ResponseEntity<ApplicationSettingsDTO> get() {
        return ResponseEntity.ok(this.applicationSettingsService.get());
    }

    @Override
    public ResponseEntity<ApplicationSettingsDTO> update(ApplicationSettingsDTO applicationSettingsDTO) {
        return ResponseEntity.ok(this.applicationSettingsService.update(applicationSettingsDTO));
    }
}
