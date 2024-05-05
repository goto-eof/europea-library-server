package com.andreidodu.europealibrary.dto.common;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommonFileSystemItemDTO extends CommonDTO {
    private String name;
    private Long size;
    // private String basePath;
    private LocalDateTime fileCreateDate;
    private LocalDateTime fileUpdateDate;
    private Boolean isDirectory;
    private Long downloadCount;
    private FileSystemItemDTO parent;
    private Integer jobStep;
    private Integer jobStatus;
    private String extension;
    private List<FileSystemItemDTO> childrenList;
}
