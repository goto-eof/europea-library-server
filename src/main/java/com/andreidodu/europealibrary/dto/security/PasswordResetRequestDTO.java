package com.andreidodu.europealibrary.dto.security;

import com.andreidodu.europealibrary.constants.RegexConst;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDTO {

    @Size(min = 10, max = 200)
    private String resetToken;

    @Pattern(regexp = RegexConst.PASSWORD)
    private String password;
}
