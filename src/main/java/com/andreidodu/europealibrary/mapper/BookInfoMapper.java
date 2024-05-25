package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.BookInfoDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class BookInfoMapper {

    public abstract BookInfoDTO toDTO(BookInfo model);

    @Mapping(ignore = true, target = "categoryList")
    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "fileSystemItemTopRatedView")
    public abstract BookInfo toModel(BookInfoDTO dto);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "categoryList")
    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "imageUrl")
    @Mapping(ignore = true, target = "fileSystemItemTopRatedView")
    public abstract void map(@MappingTarget BookInfo target, BookInfoDTO source);


}
