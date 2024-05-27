package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerAddressDTO extends CommonDTO {
    @NotNull
    @Size(min = 1, max = 100)
    private String city;

    @NotNull
    @Size(min = 1, max = 100)
    private String country;

    @NotNull
    @Size(min = 1, max = 500)
    private String line1;

    private String line2;

    @NotNull
    @Size(min = 1, max = 10)
    private String postalCode;

    @NotNull
    @Size(min = 1, max = 100)
    private String state;
}
