package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.ApplicationSettingsDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemHighlightDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.model.ApplicationSettings;

public interface ApplicationSettingsService {
    ApplicationSettingsDTO lockApplication();

    ApplicationSettingsDTO unlockApplication();

    boolean isApplicationLocked();

    ApplicationSettingsDTO get();

    ApplicationSettingsDTO update(ApplicationSettingsDTO applicationSettingsDTO);

    ApplicationSettings loadOrCreateDefaultApplicationSettings();

    FileSystemItemHighlightDTO getFeatured();

    OperationStatusDTO setFeatured(Long fileSystemItemId);

    OperationStatusDTO isFeatured(Long fileSystemItemId);
}
