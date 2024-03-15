package com.andreidodu.europealibrary.batch.step.file;

import com.andreidodu.europealibrary.dto.FileDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class FileItemProcessor implements ItemProcessor<File, FileDTO> {
    @Override
    public FileDTO process(final File file) {
        System.out.println("File processed: " + file.getAbsoluteFile());
        try {
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
            return fileDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
