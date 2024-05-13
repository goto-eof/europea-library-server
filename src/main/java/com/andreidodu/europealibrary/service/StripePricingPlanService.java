package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePricingPlanDTO;
import com.stripe.exception.StripeException;

public interface StripePricingPlanService {
    StripePricingPlanDTO getStripePricingPlan(Long stripePricingPlanId);

    StripePricingPlanDTO createStripePricingPlan(StripePricingPlanDTO stripePricingPlanDTO) throws StripeException;

    StripePricingPlanDTO updateStripePricingPlan(StripePricingPlanDTO stripePricingPlanDTO) throws StripeException;

    OperationStatusDTO deleteStripePricingPlan(Long stripePricingPlanId);
}
