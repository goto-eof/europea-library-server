package com.andreidodu.europealibrary.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO extends AuthRequestDTO {

    private String email;

}
