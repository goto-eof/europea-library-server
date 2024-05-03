package com.andreidodu.europealibrary.dto.auth;

import com.andreidodu.europealibrary.constants.RegexConst;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryChangePasswordRequestDTO extends AuthRequestDTO {

    @Size(min = 10, max = 200)
    private String recoveryString;

    @Pattern(regexp = RegexConst.PASSWORD)
    private String newPassword;
}
