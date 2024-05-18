package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripePurchaseSessionDTO;
import com.andreidodu.europealibrary.model.stripe.StripePurchaseSession;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripeProductMapper.class})
public abstract class StripePurchaseSessionMapper {

    @Mapping(ignore = true, target = "stripeCustomer")
    @Mapping(ignore = true, target = "stripePrice")
    @Mapping(ignore = true, target = "stripeCustomerProductsOwned")
    @Mapping(ignore = true, target = "stripePaymentIntentId")
    public abstract StripePurchaseSession toModel(StripePurchaseSessionDTO dto);

    @Mapping(ignore = true, target = "stripeCustomer.stripeCustomerProductsOwnedList")
    public abstract StripePurchaseSessionDTO toDTO(StripePurchaseSession model);

}
