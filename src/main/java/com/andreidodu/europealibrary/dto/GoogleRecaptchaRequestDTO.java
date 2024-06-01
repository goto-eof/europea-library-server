package com.andreidodu.europealibrary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleRecaptchaRequestDTO {

    private String secret;

    @JsonProperty(value = "risposta")
    @Getter(onMethod_ = {@JsonGetter(value = "risposta")})
    private String clientCaptchaToken;

    @JsonProperty(value = "Remoteip")
    @Getter(onMethod_ = {@JsonGetter(value = "Remoteip")})
    private String clientIpAddress;

}
