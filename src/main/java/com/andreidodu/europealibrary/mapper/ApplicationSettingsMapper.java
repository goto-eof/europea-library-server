package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.ApplicationSettingsDTO;
import com.andreidodu.europealibrary.model.ApplicationSettings;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class ApplicationSettingsMapper {

    @Mapping(ignore = true, target = "featuredFileMetaInfo")
    public abstract ApplicationSettings toModel(ApplicationSettingsDTO dto);

    @Mapping(target = "featuredFileMetaInfoId", source = "featuredFileMetaInfo.id")
    public abstract ApplicationSettingsDTO toDTO(ApplicationSettings model);

    public abstract List<ApplicationSettingsDTO> toDTO(List<ApplicationSettings> modelList);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "applicationLock")
    @Mapping(ignore = true, target = "featuredFileMetaInfo")
    public abstract void map(@MappingTarget ApplicationSettings target, ApplicationSettingsDTO source);

}
