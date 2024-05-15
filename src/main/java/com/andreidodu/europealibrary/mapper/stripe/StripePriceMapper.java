package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.model.stripe.StripePrice;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {PriceConvertMapper.class})
public abstract class StripePriceMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "stripeProduct")
    @Mapping(ignore = true, target = "stripePriceId")
    @Mapping(ignore = true, target = "stripePurchaseSessionHistoryList")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "priceNormalize")
    public abstract StripePrice toModel(StripePriceDTO dto);

    @Mapping(target = "stripeProduct", ignore = true)
    @Mapping(target = "amount", source = "amount", qualifiedByName = "priceDeNormalize")
    public abstract StripePriceDTO toDTO(StripePrice model);

    @Mapping(target = "stripeProduct", ignore = true)
    @Mapping(target = "stripePriceId", ignore = true)
    @Mapping(target = "stripePurchaseSessionHistoryList", ignore = true)
    @Mapping(target = "amount", source = "amount", qualifiedByName = "priceNormalize")
    public abstract void map(@MappingTarget StripePrice target, StripePriceDTO source);


}
