package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StripeCustomerDTO extends CommonDTO {

    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    @Size(min = 5, max = 302)
    private String email;

    private UserDTO user;

    private List<StripeCustomerProductsOwnedDTO> stripeCustomerProductsOwnedList;

    @NotNull
    private StripeCustomerAddressDTO currentStripeCustomerAddress;

    private List<StripeCustomerAddressDTO> stripeCustomerAddressList;
}
