package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileExtensionDTO;
import com.andreidodu.europealibrary.model.FileExtensionProjection;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FileExtensionMapper {
    public abstract FileExtensionProjection toModel(FileExtensionDTO dto);

    public abstract FileExtensionDTO toDTO(FileExtensionProjection model);

    public abstract List<FileExtensionDTO> toDTO(List<FileExtensionProjection> modelList);
}
