package com.andreidodu.europealibrary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class GoogleRecaptchaResponseDTO {

    private boolean success;

    @JsonProperty(value = "challenge_ts")
    @Getter(onMethod_ = {@JsonGetter(value = "challenge_ts")})
    private LocalDateTime challengeTS;

    private String hostname;

    @JsonProperty(value = "error-codes")
    @Getter(onMethod_ = {@JsonGetter(value = "error-codes")})
    private List<String> errorCodes;
}
