package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class FileSystemMapper {

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "parent", ignore = true)
//    @Mapping(target = "childrenList", ignore = true)
//    @Mapping(target = "jobStep", ignore = true)
//    @Mapping(target = "jobStatus", ignore = true)
//    @Mapping(target = "fileMetaInfoList", ignore = true)
//    @Mapping(target = "version", ignore = true)
    public abstract FileSystemItem toModel(FileDTO dto);
}
