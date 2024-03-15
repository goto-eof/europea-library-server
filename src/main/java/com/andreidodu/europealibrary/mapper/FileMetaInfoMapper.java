package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {TagMapper.class})
public abstract class FileMetaInfoMapper {

    public abstract FileMetaInfoDTO toDTO(FileMetaInfo model);

    public abstract void map(@MappingTarget FileMetaInfoDTO target, FileMetaInfo source);
}
