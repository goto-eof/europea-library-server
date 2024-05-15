package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.mapper.StripeProductMapper;
import com.andreidodu.europealibrary.model.stripe.StripePrice;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {})
public abstract class StripePriceMapper {

    @Mapping(ignore = true, target = "stripeProduct")
    @Mapping(ignore = true, target = "stripePriceId")
    @Mapping(ignore = true, target = "stripePurchaseSessionHistoryList")
    public abstract StripePrice toModel(StripePriceDTO dto);

    @Mapping(target = "stripeProduct", ignore = true)
    public abstract StripePriceDTO toDTO(StripePrice model);

    @Mapping(target = "stripeProduct", ignore = true)
    @Mapping(target = "stripePriceId", ignore = true)
    @Mapping(target = "stripePurchaseSessionHistoryList", ignore = true)
    public abstract void map(@MappingTarget StripePrice target, StripePriceDTO source);


}
