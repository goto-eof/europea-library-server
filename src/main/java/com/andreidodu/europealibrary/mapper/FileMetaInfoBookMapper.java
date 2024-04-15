package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {TagMapper.class, CategoryMapper.class})
public abstract class FileMetaInfoBookMapper {
    @Autowired
    private FileMetaInfoMapper fileMetaInfoMapper;
    @Autowired
    private FileSystemItemMapper fileSystemItemMapper;
    @Autowired
    private CategoryMapper categoryMapper;


    public FileMetaInfoBookDTO toDTO(FileMetaInfo fileMetaInfo) {
        return Optional.ofNullable(fileMetaInfo)
                .map(model -> {
                    FileMetaInfoBookDTO book = new FileMetaInfoBookDTO();
                    this.map(book, fileMetaInfo.getBookInfo());
                    this.fileMetaInfoMapper.map(book, fileMetaInfo);
                    book.setFileSystemItemIdList(fileMetaInfo.getFileSystemItemList()
                            .stream()
                            .map(FileSystemItem::getId)
                            .toList());
                    return book;
                }).orElse(null);

    }

    @Mapping(ignore = true, target = "title")
    @Mapping(ignore = true, target = "description")
    @Mapping(ignore = true, target = "tagList")
    @Mapping(ignore = true, target = "fileSystemItemIdList")
    public abstract void map(@MappingTarget FileMetaInfoBookDTO dto, BookInfo source);

    public void map(@MappingTarget FileMetaInfo model, FileMetaInfoBookDTO dto) {
        this.fileMetaInfoMapper.map(model, dto);
        this.map(model.getBookInfo(), dto);
    }

    @Mapping(ignore = true, target = "fileMetaInfo")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "recordStatus")
    @Mapping(ignore = true, target = "isCorrupted")
    public abstract void map(@MappingTarget BookInfo model, FileMetaInfoBookDTO source);

    public FileMetaInfo toModel(FileMetaInfoBookDTO dto) {
        FileMetaInfo model = new FileMetaInfo();
        model.setBookInfo(new BookInfo());
        model.getBookInfo().setFileMetaInfo(model);
        fileMetaInfoMapper.map(model, dto);
        this.map(model.getBookInfo(), dto);
        return model;
    }
}
