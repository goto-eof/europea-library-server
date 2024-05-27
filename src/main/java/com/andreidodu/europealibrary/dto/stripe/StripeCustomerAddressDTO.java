package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerAddressDTO extends CommonDTO {
    @Size(min = 1, max = 100)
    private String city;
    @Size(min = 1, max = 100)
    private String country;
    @Size(min = 1, max = 500)
    private String line1;
    @Size(min = 1, max = 500)
    private String line2;
    @Size(min = 1, max = 10)
    private String postalCode;
    @Size(min = 1, max = 100)
    private String state;
}
