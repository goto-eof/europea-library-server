package com.andreidodu.europealibrary.dto.security;

import com.andreidodu.europealibrary.annotation.validation.ShouldBeAlwaysTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO extends AuthRequestDTO {

    @Size(min = 5, max = 302)
    private String email;

    @ShouldBeAlwaysTrue
    private Boolean consensus1Flag;

    @ShouldBeAlwaysTrue
    private Boolean consensus2Flag;

    @NotNull
    private Boolean consensus3Flag;

}
