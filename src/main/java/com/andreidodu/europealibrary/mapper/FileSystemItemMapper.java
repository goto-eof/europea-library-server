package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.CursoredFileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

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
    @Mapping(ignore = true, target = "recordStatus")
    public abstract FileSystemItem toModel(FileDTO dto);

    @Mapping(ignore = true, target = "childrenList")
    @Mapping(ignore = true, target = "parent")
    public abstract FileSystemItemDTO toDTOWithoutChildrenAndParent(FileSystemItem model);

    @Named(value = "parentToDTO")
    @Mapping(ignore = true, target = "childrenList")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "parentToDTO")
    public abstract FileSystemItemDTO toDTOParent(FileSystemItem model);

    @Named("toDTOWithChildAndParent")
    public FileSystemItemDTO toDTO(FileSystemItem model) {
        FileSystemItemDTO dto = this.toDTOWithoutChildrenAndParent(model);
        dto.setChildrenList(toDTOWithoutChildrenAndParent(model.getChildrenList()));
        dto.setParent(this.toDTOParent(model.getParent()));
        return dto;
    }

    public List<FileSystemItemDTO> toDTO(List<FileSystemItem> list) {
        return list.stream().map(this::toDTOWithoutChildrenAndParent).toList();
    }


    @Named("toDTOWithoutChildrenAndParent")
    public List<FileSystemItemDTO> toDTOWithoutChildrenAndParent(List<FileSystemItem> fileSystemItemList) {
        return fileSystemItemList.stream().map(this::toDTOWithoutChildrenAndParent).toList();
    }


    @Named("toCursoredDTO")
    @MapMapping(valueQualifiedByName = "toDTOWithChildAndParent")
    @Mapping(ignore = true, target = "nextCursor")
    public CursoredFileSystemItemDTO toCursoredDTO(FileSystemItem model) {
        FileSystemItemDTO dto = this.toDTOWithoutChildrenAndParent(model);
        CursoredFileSystemItemDTO cursoredFileSystemItemDTO = new CursoredFileSystemItemDTO();
        this.map(cursoredFileSystemItemDTO, dto);
        cursoredFileSystemItemDTO.setChildrenList(toDTOWithoutChildrenAndParent(model.getChildrenList()));
        cursoredFileSystemItemDTO.setParent(this.toDTOParent(model.getParent()));
        this.toParentDTORecursively(cursoredFileSystemItemDTO.getParent(), model.getParent());
        return cursoredFileSystemItemDTO;
    }


    public void toParentDTORecursively(FileSystemItemDTO dto, FileSystemItem model) {
        if (model == null) {
            return;
        }
        dto.setParent(this.toDTOParent(model.getParent()));
        toParentDTORecursively(dto.getParent(), model.getParent());
    }

    @Mapping(ignore = true, target = "nextCursor")
    public abstract void map(@MappingTarget CursoredFileSystemItemDTO cursoredFileSystemItemDTO, FileSystemItemDTO parent);


    @Mapping(ignore = true, target = "nextCursor")
    @MapMapping(valueQualifiedByName = "toCursoredDTO")
    public abstract void map(@MappingTarget CursoredFileSystemItemDTO cursoredFileSystemItemDTO, FileSystemItem parent);
}
