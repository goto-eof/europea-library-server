package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.andreidodu.europealibrary.enums.StripePurchaseSessionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripePurchaseSessionDTO extends CommonDTO {

    private Long id;
    private StripePurchaseSessionStatus stripePurchaseSessionStatus;
    private StripeCustomerDTO stripeCustomer;
    private StripeProductDTO stripeProduct;
    private StripePricingPlanDTO stripePricingPlan;


}
