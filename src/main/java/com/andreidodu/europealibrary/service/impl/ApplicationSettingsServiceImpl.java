package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationSettingsConst;
import com.andreidodu.europealibrary.dto.ApplicationSettingsDTO;
import com.andreidodu.europealibrary.mapper.ApplicationSettingsMapper;
import com.andreidodu.europealibrary.model.ApplicationSettings;
import com.andreidodu.europealibrary.repository.ApplicationSettingsRepository;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationSettingsServiceImpl implements ApplicationSettingsService {
    final private ApplicationSettingsMapper applicationSettingsMapper;
    final private ApplicationSettingsRepository applicationSettingsRepository;

    @Override
    public ApplicationSettingsDTO lockApplication() {
        return changeApplicationLock(true);
    }

    @Override
    public ApplicationSettingsDTO unlockApplication() {
        return changeApplicationLock(false);
    }

    @Override
    public boolean isApplicationLocked() {
        return this.loadOrCreateDefaultApplicationSettings().getApplicationLock();
    }

    private ApplicationSettingsDTO changeApplicationLock(boolean isLocked) {
        ApplicationSettings applicationSettings = this.loadOrCreateDefaultApplicationSettings();
        applicationSettings.setApplicationLock(isLocked);
        applicationSettings = this.applicationSettingsRepository.save(applicationSettings);
        return this.applicationSettingsMapper.toDTO(applicationSettings);
    }

    private ApplicationSettings loadOrCreateDefaultApplicationSettings() {
        Optional<ApplicationSettings> applicationSettingsOptional = this.applicationSettingsRepository.findAll().stream().findFirst();
        if (applicationSettingsOptional.isEmpty()) {
            ApplicationSettings applicationSettings = new ApplicationSettings();
            applicationSettings.setApplicationLock(ApplicationSettingsConst.APPLICATION_LOCK);
            return this.applicationSettingsRepository.save(applicationSettings);
        }
        return applicationSettingsOptional.get();
    }

}
