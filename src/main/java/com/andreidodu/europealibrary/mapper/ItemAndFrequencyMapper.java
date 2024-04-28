package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileExtensionDTO;
import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.model.FileExtensionProjection;
import com.andreidodu.europealibrary.model.ItemAndFrequencyProjection;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class ItemAndFrequencyMapper {
    public abstract ItemAndFrequencyProjection toModel(ItemAndFrequencyDTO dto);

    public abstract ItemAndFrequencyDTO toDTO(ItemAndFrequencyProjection model);

    public abstract List<ItemAndFrequencyDTO> toDTO(List<ItemAndFrequencyProjection> modelList);
}
