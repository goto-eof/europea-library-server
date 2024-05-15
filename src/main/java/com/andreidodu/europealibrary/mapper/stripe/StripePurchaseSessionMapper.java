package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripePurchaseSessionDTO;
import com.andreidodu.europealibrary.model.stripe.StripePurchaseSession;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripeProductMapper.class, StripePricingPlanMapper.class})
public abstract class StripePurchaseSessionMapper {

    @Mapping(ignore = true, target = "stripeCustomer")
    @Mapping(ignore = true, target = "stripePrice")
    public abstract StripePurchaseSession toModel(StripePurchaseSessionDTO dto);

    public abstract StripePurchaseSessionDTO toDTO(StripePurchaseSession model);

}
