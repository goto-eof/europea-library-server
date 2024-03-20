package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class FileDTO extends CommonDTO {

    private String name;
    private Long size;
    private String basePath;
    private LocalDateTime fileCreateDate;
    private LocalDateTime fileUpdateDate;
    private Boolean isDirectory;
    private String extension;
    private String sha256;
}
