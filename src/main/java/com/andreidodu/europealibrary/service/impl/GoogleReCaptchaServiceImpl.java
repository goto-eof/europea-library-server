package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.client.GoogleReCaptchaClient;
import com.andreidodu.europealibrary.dto.GoogleRecaptchaResponseDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.service.GoogleReCaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleReCaptchaServiceImpl implements GoogleReCaptchaService {

    @Value("${com.andreidodu.europea-library.google.recaptcha.secret}")
    private String captchaSecret;

    private final GoogleReCaptchaClient googleReCaptchaClient;

    @Override
    public void verify(String remoteAddress, String clientCaptchaToken) {
        GoogleRecaptchaResponseDTO googleRecaptchaResponseDTO = this.googleReCaptchaClient.verify(captchaSecret, clientCaptchaToken, remoteAddress);
        if (!googleRecaptchaResponseDTO.isSuccess()) {
            throw new ValidationException("success value is false: " + googleRecaptchaResponseDTO.toString());
        }
        if (googleRecaptchaResponseDTO.getScore() < 0.5) {
            throw new ValidationException("Score too low: " + googleRecaptchaResponseDTO.toString());
        }
    }

}
