package com.andreidodu.europealibrary.dto.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO extends AuthRequestDTO {

    @Min(5)
    @Max(320)
    private String email;

}
