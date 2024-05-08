package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.model.ApplicationSettings;
import com.andreidodu.europealibrary.repository.ApplicationSettingsRepository;
import com.andreidodu.europealibrary.service.CustomAuthorityCalculatorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("customAuthorityCalculatorService")
@Transactional
@RequiredArgsConstructor
public class CustomAuthorityCalculatorServiceImpl implements CustomAuthorityCalculatorService {
    private final ApplicationSettingsRepository applicationSettingsRepository;

    @Override
    public boolean isProtectedDownloadsDisabled() {
        ApplicationSettings applicationSettings = this.applicationSettingsRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApplicationException("Internal error"));
        boolean isProtectedDownloadsEnabled = applicationSettings.getProtectedDownloadsEnabled() != null && applicationSettings.getProtectedDownloadsEnabled();
        return !isProtectedDownloadsEnabled;
    }
}
