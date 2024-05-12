package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.StripeCustomerPricingPlanOwnedDTO;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerPricingPlanOwned;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripePricingPlanMapper.class})
public abstract class StripeCustomerPricingPlanOwnedMapper {

    @Mapping(ignore = true, target = "stripeCustomer")
    public abstract StripeCustomerPricingPlanOwned toModel(StripeCustomerPricingPlanOwnedDTO dto);

    public abstract StripeCustomerPricingPlanOwnedDTO toDTO(StripeCustomerPricingPlanOwned model);

}
