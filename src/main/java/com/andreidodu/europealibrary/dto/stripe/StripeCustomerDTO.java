package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StripeCustomerDTO extends CommonDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserDTO user;
    private List<StripeCustomerProductsOwnedDTO> stripeCustomerProductsOwnedList;
    private StripeCustomerAddressDTO currentStripeCustomerAddress;
    private List<StripeCustomerAddressDTO> stripeCustomerAddressList;
}
