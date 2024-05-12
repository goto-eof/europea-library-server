package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeUserRegistrationRequestDTO extends CommonDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Long userId;

}
