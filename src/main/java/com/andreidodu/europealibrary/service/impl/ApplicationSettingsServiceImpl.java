package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationSettingsConst;
import com.andreidodu.europealibrary.dto.ApplicationSettingsDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
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

    @Override
    public ApplicationSettingsDTO get() {
        return this.applicationSettingsMapper.toDTO(retrieveApplicationSettings());
    }

    private ApplicationSettings retrieveApplicationSettings() {
        return this.applicationSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ApplicationException("settings not yet defined"));
    }

    @Override
    public ApplicationSettingsDTO update(ApplicationSettingsDTO applicationSettingsDTO) {
        ApplicationSettings applicationSettings = this.retrieveApplicationSettings();
        this.applicationSettingsMapper.map(applicationSettings, applicationSettingsDTO);
        applicationSettings = this.applicationSettingsRepository.save(applicationSettings);
        return this.applicationSettingsMapper.toDTO(applicationSettings);
    }

    private ApplicationSettingsDTO changeApplicationLock(boolean isLocked) {
        ApplicationSettings applicationSettings = this.loadOrCreateDefaultApplicationSettings();
        applicationSettings.setApplicationLock(isLocked);
        applicationSettings = this.applicationSettingsRepository.save(applicationSettings);
        return this.applicationSettingsMapper.toDTO(applicationSettings);
    }

    @Override
    public ApplicationSettings loadOrCreateDefaultApplicationSettings() {
        Optional<ApplicationSettings> applicationSettingsOptional = this.applicationSettingsRepository.findAll().stream().findFirst();
        if (applicationSettingsOptional.isEmpty()) {
            ApplicationSettings applicationSettings = new ApplicationSettings();
            applicationSettings.setApplicationLock(ApplicationSettingsConst.APPLICATION_LOCK);
            applicationSettings.setCustomDescriptionEnabled(true);
            applicationSettings.setFeaturedBooksWidgetEnabled(true);
            applicationSettings.setNewBooksWidgetEnabled(true);
            applicationSettings.setPopularBooksWidgetEnabled(true);
            applicationSettings.setFeaturedBookWidgetEnabled(true);
            applicationSettings.setProtectedDownloadsEnabled(true);
            return this.applicationSettingsRepository.save(applicationSettings);
        }
        return applicationSettingsOptional.get();
    }

}
