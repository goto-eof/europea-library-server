package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.mapper.FileMetaInfoBookMapper;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {FileMetaInfoBookMapper.class})
public abstract class StripeProductMapper {

    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "stripeProductId")
    @Mapping(ignore = true, target = "stripePrice")
    public abstract StripeProduct toModel(StripeProductDTO dto);

    @Mapping(source = "fileMetaInfo.id", target = "fileMetaInfoId")
    @Mapping(ignore = true, target = "stripePrice")
    public abstract StripeProductDTO toDTO(StripeProduct model);

    @Mapping(target = "fileMetaInfo", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stripeProductId", ignore = true)
    @Mapping(target = "stripePrice", ignore = true)
    public abstract void map(@MappingTarget StripeProduct stripeProduct, StripeProductDTO stripeProductDTO);
}
