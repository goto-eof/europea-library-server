package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {})
public abstract class StripeProductMapper {

    @Mapping(source = "fileMetaInfo.id", target = "fileMetaInfoId")
    public abstract StripeProductDTO toDTO(StripeProduct model);

    @Mapping(ignore = true, target = "stripeProductId")
    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "stripePriceHistoryList")
    @Mapping(ignore = true, target = "currentStripePrice")
    public abstract StripeProduct toModel(StripeProductDTO dto);

    @Mapping(source = "fileMetaInfo.id", target = "fileMetaInfoId")
    public abstract void map(@MappingTarget StripeProductDTO stripeProductDTO, StripeProduct stripeProduct);

    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "stripePriceHistoryList")
    @Mapping(ignore = true, target = "stripeProductId")
    @Mapping(ignore = true, target = "currentStripePrice")
    public abstract void map(@MappingTarget StripeProduct stripeProduct, StripeProductDTO stripeProductDTO);


}
