package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FileSystemItemDTO extends CommonDTO {
    private String name;
    private Long size;
    private String basePath;
    private LocalDateTime fileCreateDate;
    private LocalDateTime fileUpdateDate;
    private Boolean isDirectory;
    private FileSystemItemDTO parent;
    private List<FileSystemItemDTO> childrenList;
    private Integer jobStep;
    private Integer jobStatus;
}
