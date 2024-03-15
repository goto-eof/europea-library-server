package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class TagMapper {

    @Mapping(ignore = true, target = "fileMetaInfoList")
    @Mapping(ignore = true, target = "id")
    public abstract Tag toModel(TagDTO dto);

    public abstract TagDTO toDTO(Tag model);

    public abstract List<TagDTO> toDTO(List<Tag> modelList);
}
