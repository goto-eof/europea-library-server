package com.andreidodu.europealibrary.dto.security;

import com.andreidodu.europealibrary.constants.RegexConst;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDTO extends AuthRequestDTO {

    @Pattern(regexp = RegexConst.PASSWORD)
    private String oldPassword;

    @Pattern(regexp = RegexConst.PASSWORD)
    private String newPassword;
}
