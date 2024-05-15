package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {TagMapper.class, BookInfoMapper.class, StripeProductMapper.class})
public abstract class FileMetaInfoMapper {

    public abstract FileMetaInfoDTO toDTO(FileMetaInfo model);

    public abstract void map(@MappingTarget FileMetaInfoDTO target, FileMetaInfo source);

    @Mapping(ignore = true, target = "fileSystemItemList")
    @Mapping(ignore = true, target = "bookInfo")
    @Mapping(ignore = true, target = "tagList")
    @Mapping(ignore = true, target = "stripeProduct")
    public abstract void map(@MappingTarget FileMetaInfo target, FileMetaInfoDTO source);

}
