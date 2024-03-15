package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FileMetaInfoBookMapper {
    @Autowired
    private FileMetaInfoMapper fileMetaInfoMapper;
    @Autowired
    private FileSystemItemMapper fileSystemItemMapper;


    public FileMetaInfoBookDTO toDTO(FileMetaInfo fileMetaInfo) {
        FileMetaInfoBookDTO book = new FileMetaInfoBookDTO();
        this.map(book, fileMetaInfo.getBookInfo());
        this.fileMetaInfoMapper.map(book, fileMetaInfo);
        book.setFileSystemItemList(fileSystemItemMapper.toDTO(fileMetaInfo.getFileSystemItemList()));
        return book;
    }

    public abstract void map(@MappingTarget FileMetaInfoBookDTO dto, BookInfo source);

}
