package com.andreidodu.europealibrary.config;

import com.andreidodu.europealibrary.exception.WorkInProgressException;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AnyHandlerInterceptor implements HandlerInterceptor {
    private final ApplicationSettingsService applicationSettingsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (this.applicationSettingsService.isApplicationLocked()) {
            throw new WorkInProgressException("The application is indexing files. Please try later.");
        }
        return true;
    }
}