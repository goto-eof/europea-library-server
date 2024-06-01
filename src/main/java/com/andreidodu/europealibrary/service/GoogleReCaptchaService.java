package com.andreidodu.europealibrary.service;

public interface GoogleReCaptchaService {
    void verify(String remoteAddress, String clientCaptchaToken);
}
