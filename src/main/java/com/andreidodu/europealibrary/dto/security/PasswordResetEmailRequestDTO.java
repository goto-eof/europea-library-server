package com.andreidodu.europealibrary.dto.security;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetEmailRequestDTO {

    @Size(min = 5, max = 302)
    private String email;

}
