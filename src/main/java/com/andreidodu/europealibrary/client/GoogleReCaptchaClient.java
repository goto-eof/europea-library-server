package com.andreidodu.europealibrary.client;

import com.andreidodu.europealibrary.dto.GoogleRecaptchaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "google-recaptcha", url = "${com.andreidodu.europea-library.google.recaptcha.query-url}")
public interface GoogleReCaptchaClient {

    @RequestMapping(method = RequestMethod.POST)
    GoogleRecaptchaResponseDTO verify(@RequestParam("secret") String secret, @RequestParam("response") String response, @RequestParam("remoteip") String remoteIp);

}
