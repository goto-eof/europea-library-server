package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonFileSystemItemDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoredFileSystemItemDTO extends CommonFileSystemItemDTO {
    private CursorDTO<FileSystemItemDTO> cursor;
}
