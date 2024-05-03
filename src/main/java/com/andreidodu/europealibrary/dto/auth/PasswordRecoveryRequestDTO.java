package com.andreidodu.europealibrary.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRecoveryRequestDTO {

    @Size(min = 5, max = 302)
    private String email;

}
