package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.util.DateUtil;
import com.andreidodu.europealibrary.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FileMapper {

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private DateUtil dateUtil;

    public FileDTO toDTO(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime fileCreationTime = attr.creationTime();
        FileTime fileUpdateTime = attr.lastModifiedTime();
        FileDTO fileDTO = new FileDTO();
        LocalDateTime creationDateTime = dateUtil.fileTimeToLocalDateTime(fileCreationTime);
        LocalDateTime updateDateTime = dateUtil.fileTimeToLocalDateTime(fileUpdateTime);
        fileDTO.setFileCreateDate(creationDateTime);
        fileDTO.setFileUpdateDate(updateDateTime);
        fileDTO.setSize(attr.size());
        fileDTO.setName(file.getName());
        fileDTO.setIsDirectory(file.isDirectory());
        fileDTO.setBasePath(file.getParentFile().getAbsolutePath());
        if (file.isFile()) {
            this.fileUtil.fileToSha256(file)
                    .ifPresent(fileDTO::setSha256);
        }
        return fileDTO;
    }


}
