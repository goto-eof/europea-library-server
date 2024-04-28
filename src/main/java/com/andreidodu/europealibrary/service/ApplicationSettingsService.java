package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.ApplicationSettingsDTO;

public interface ApplicationSettingsService {
    ApplicationSettingsDTO lockApplication();

    ApplicationSettingsDTO unlockApplication();

    boolean isApplicationLocked();
}
