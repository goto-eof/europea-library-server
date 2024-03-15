package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileMetaInfoBookDTO extends FileMetaInfoDTO {
    private String imageUrl;
    private String authors;
    private String note;
    private String sbn;
    private String isbn;
    private String publisher;
    private Integer year;
    private List<TagDTO> tagList;
    private List<FileSystemItemDTO> fileSystemItemList;
}
