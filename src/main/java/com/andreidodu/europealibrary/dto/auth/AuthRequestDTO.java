package com.andreidodu.europealibrary.dto.auth;

import com.andreidodu.europealibrary.constants.RegexConst;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {

    @Pattern(regexp = RegexConst.USERNAME )
    private String username;

    @Pattern(regexp = RegexConst.PASSWORD)
    private String password;

}
