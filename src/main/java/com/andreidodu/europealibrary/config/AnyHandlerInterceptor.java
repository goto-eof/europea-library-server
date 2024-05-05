package com.andreidodu.europealibrary.config;

import com.andreidodu.europealibrary.exception.WorkInProgressException;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnyHandlerInterceptor implements HandlerInterceptor {
    private final ApplicationSettingsService applicationSettingsService;

    @Value("${com.andreidodu.europea-library.allowed-urls-when-job-is-running}")
    private List<String> allowedUrlsWhenJobIsRunning;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());

        List<String> allowedUrls = this.allowedUrlsWhenJobIsRunning.stream().map(String::toLowerCase).toList();
        String url = requestURL.toString().toLowerCase();
        final boolean isExceptionalMethod = allowedUrls.stream().anyMatch(url::endsWith);
        if (this.applicationSettingsService.isApplicationLocked() && !isExceptionalMethod) {
            throw new WorkInProgressException("The application is indexing files. Please try later.");
        }
        return true;
    }
}