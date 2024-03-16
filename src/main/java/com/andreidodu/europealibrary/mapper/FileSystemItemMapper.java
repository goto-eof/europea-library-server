package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FileSystemItemMapper {


    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "parent")
    @Mapping(ignore = true, target = "childrenList")
    @Mapping(ignore = true, target = "jobStep")
    @Mapping(ignore = true, target = "jobStatus")
    @Mapping(ignore = true, target = "fileMetaInfo")
    public abstract FileSystemItem toModel(FileDTO dto);

    @Mapping(ignore = true, target = "childrenList")
    @Mapping(ignore = true, target = "parent")
    public abstract FileSystemItemDTO toDTOWithoutChildren(FileSystemItem model);

    public FileSystemItemDTO toDTO(FileSystemItem model) {
        FileSystemItemDTO dto = this.toDTOWithoutChildren(model);
        dto.setChildrenList(toDTOWithoutChildren(model.getChildrenList()));
        dto.setParent(this.toDTOWithoutChildren(model.getParent()));
        return dto;
    }


    public  List<FileSystemItemDTO> toDTOWithoutChildren(List<FileSystemItem> fileSystemItemList){
        return fileSystemItemList.stream().map(this::toDTOWithoutChildren).toList();
    }
}
