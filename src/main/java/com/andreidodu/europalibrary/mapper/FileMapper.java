package com.andreidodu.europalibrary.mapper;

import com.andreidodu.europalibrary.dto.FileDTO;
import com.andreidodu.europalibrary.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class FileMapper {
    public FileDTO toDTO(File file) {
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(Path.of(file.getPath()), BasicFileAttributes.class);
            FileDTO dto = new FileDTO();
            dto.setFilename(file.getName());
            dto.setFileDate(new Date(fileAttributes.creationTime().toMillis()));
            dto.setFileSize(fileAttributes.size());
            return dto;
        } catch (IOException e) {
            log.error("Unable to read file attributes: " + e.getMessage());
            throw new ApplicationException(e);
        }
    }
}
