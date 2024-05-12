package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.StripeProductDTO;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {FileMetaInfoBookMapper.class})
public abstract class StripeProductMapper {

    @Mapping(ignore = true, target = "fileMetaInfo")
    public abstract StripeProduct toModel(StripeProductDTO dto);

    public abstract StripeProductDTO toDTO(StripeProduct model);

}
