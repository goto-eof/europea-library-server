package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.andreidodu.europealibrary.model.security.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StripeCustomerDTO extends CommonDTO {

    private Long id;
    private String stripeCustomerId;
    private String firstName;
    private String lastName;
    private User user;
    private List<StripeCustomerProductsOwnedDTO> stripeCustomerProductsOwnedList;
    private List<StripeCustomerPricingPlanOwnedDTO> stripeCustomerPricingPlanOwnedList;
}
