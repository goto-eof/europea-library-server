package com.andreidodu.europealibrary.mapper.stripe;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class PriceConvertMapper {

    @Named(value = "priceNormalize")
    public BigDecimal priceNormalize(BigDecimal in) {
        if (in == null) {
            return null;
        }
        return in.multiply(BigDecimal.valueOf(100));
    }

    @Named(value = "priceDeNormalize")
    public BigDecimal priceDeNormalize(BigDecimal in) {
        if (in == null) {
            return null;
        }
        return in.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

}
