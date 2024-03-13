package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class FileMapper {
    public FileDTO toDTO(File file) {
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(Path.of(file.getPath()), BasicFileAttributes.class);
            FileDTO dto = new FileDTO();

            return dto;
        } catch (IOException e) {
            log.error("Unable to read file attributes: " + e.getMessage());
            throw new ApplicationException(e);
        }
    }
}
