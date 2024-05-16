package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerAddressDTO extends CommonDTO {
    private String city;
    private String country;
    private String line1;
    private String line2;
    private String postalCode;
    private String state;
}
