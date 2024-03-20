package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FileMapper {
    @Autowired
    private FileUtil fileUtil;

    public FileDTO toDTO(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime fileCreationTime = attr.creationTime();
        FileTime fileUpdateTime = attr.lastModifiedTime();
        FileDTO fileDTO = new FileDTO();
        LocalDateTime creationDateTime = Instant.ofEpochMilli(fileCreationTime.toMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updateDateTime = Instant.ofEpochMilli(fileUpdateTime.toMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        fileDTO.setFileCreateDate(creationDateTime);
        fileDTO.setFileUpdateDate(updateDateTime);
        fileDTO.setSize(attr.size());
        fileDTO.setName(file.getName());
        fileDTO.setIsDirectory(file.isDirectory());
        fileDTO.setBasePath(file.getParentFile().getAbsolutePath());
        if (file.isFile()) {
            fileDTO.setSha256(this.fileUtil.fileToSha256(file));
        }
        return fileDTO;
    }
}
