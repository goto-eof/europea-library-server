package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerPricingPlanOwnedDTO extends CommonDTO {

    private Long id;
    private StripePricingPlanDTO stripePricingPlan;

}
