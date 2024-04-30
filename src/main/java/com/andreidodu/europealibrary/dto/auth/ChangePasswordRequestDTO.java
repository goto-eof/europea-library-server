package com.andreidodu.europealibrary.dto.auth;

import com.andreidodu.europealibrary.constants.RegexConst;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
