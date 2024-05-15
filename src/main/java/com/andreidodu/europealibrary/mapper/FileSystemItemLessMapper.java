package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.CursoredFileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemHighlightDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {FileMetaInfoMapper.class})
public abstract class FileSystemItemLessMapper {


    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "parent")
    @Mapping(ignore = true, target = "childrenList")
    @Mapping(ignore = true, target = "jobStep")
    @Mapping(ignore = true, target = "jobStatus")
    @Mapping(ignore = true, target = "recordStatus")
    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "fileMetaInfoId")
    @Mapping(ignore = true, target = "parentId")
    @Mapping(ignore = true, target = "downloadCount")
    @Mapping(ignore = true, target = "featuredFileSystemItem")
    @Mapping(ignore = true, target = "applicationSettings")
    public abstract FileSystemItem toModel(FileDTO dto);

    @Mapping(ignore = true, target = "childrenList")
    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "parent")
    @Mapping(source = "fileMetaInfo.bookInfo.averageRating", target = "averageRating")
    @Mapping(source = "fileMetaInfo.bookInfo.ratingsCount", target = "ratingsCount")
    public abstract FileSystemItemDTO toDTOWithoutChildrenAndParentLess(FileSystemItem model);

    @Named(value = "parentToDTO")
    @Mapping(ignore = true, target = "childrenList")
    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "parentToDTO")
    @Mapping(source = "fileMetaInfo.bookInfo.averageRating", target = "averageRating")
    @Mapping(source = "fileMetaInfo.bookInfo.ratingsCount", target = "ratingsCount")
    public abstract FileSystemItemDTO toDTOParent(FileSystemItem model);

    @Named("toDTOWithChildAndParent")
    public FileSystemItemDTO toDTOLess(FileSystemItem model) {
        FileSystemItemDTO dto = this.toDTOWithoutChildrenAndParentLess(model);
        dto.setChildrenList(toDTOWithoutChildrenAndParentLess(model.getChildrenList()));
        dto.setParent(this.toDTOParent(model.getParent()));
        return dto;
    }

    public List<FileSystemItemDTO> toDTOLess(List<FileSystemItem> list) {
        return list.stream().map(this::toDTOWithoutChildrenAndParentLess).toList();
    }


    @Named("toDTOWithoutChildrenAndParent")
    public List<FileSystemItemDTO> toDTOWithoutChildrenAndParentLess(List<FileSystemItem> fileSystemItemList) {
        return fileSystemItemList.stream().map(this::toDTOWithoutChildrenAndParentLess).toList();
    }


    @Named("toCursoredDTO")
    @MapMapping(valueQualifiedByName = "toDTOWithChildAndParent")
    @Mapping(ignore = true, target = "nextCursor")
    public CursoredFileSystemItemDTO toCursoredDTO(FileSystemItem model) {
        FileSystemItemDTO dto = this.toDTOWithoutChildrenAndParentLess(model);
        CursoredFileSystemItemDTO cursoredFileSystemItemDTO = new CursoredFileSystemItemDTO();
        this.map(cursoredFileSystemItemDTO, dto);
        cursoredFileSystemItemDTO.setChildrenList(toDTOWithoutChildrenAndParentLess(model.getChildrenList()));
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


    public FileSystemItemDTO toDTOWithParentDTORecursively(FileSystemItem model) {
        if (model == null) {
            return null;
        }
        FileSystemItemDTO dto = this.toDTOWithoutChildrenAndParentLess(model);
        dto.setParent(this.toDTOParent(model.getParent()));
        toParentDTORecursively(dto.getParent(), model.getParent());
        return dto;
    }

    @Mapping(ignore = true, target = "nextCursor")
    public abstract void map(@MappingTarget CursoredFileSystemItemDTO cursoredFileSystemItemDTO, FileSystemItemDTO parent);


    @Mapping(source = "fileMetaInfo.bookInfo.imageUrl", target = "imageUrl")
    @Mapping(source = "fileMetaInfo.bookInfo.averageRating", target = "averageRating")
    @Mapping(source = "fileMetaInfo.bookInfo.ratingsCount", target = "ratingsCount")
    @Mapping(source = "fileMetaInfo.title", target = "title")
    @Mapping(source = "fileMetaInfo.description", target = "description")
    public abstract FileSystemItemHighlightDTO toHighlightDTO(FileSystemItem model);

}
