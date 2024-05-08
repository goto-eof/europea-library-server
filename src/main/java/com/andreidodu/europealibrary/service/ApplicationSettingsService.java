package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.ApplicationSettingsDTO;
import com.andreidodu.europealibrary.model.ApplicationSettings;

public interface ApplicationSettingsService {
    ApplicationSettingsDTO lockApplication();

    ApplicationSettingsDTO unlockApplication();

    boolean isApplicationLocked();

    ApplicationSettingsDTO get();

    ApplicationSettingsDTO update(ApplicationSettingsDTO applicationSettingsDTO);

    ApplicationSettings loadOrCreateDefaultApplicationSettings();
}
