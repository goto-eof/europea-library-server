package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemHighlightDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;

public interface FeaturedFileSystemItemService {

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursored(CursorRequestDTO cursorRequestDTO);

    OperationStatusDTO addFeatured(Long fileSystemItemId);

    OperationStatusDTO removeFeatured(Long fileSystemItemId);

    OperationStatusDTO isFeatured(Long fileSystemItemId);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredHighlight(CursorRequestDTO cursorRequestDTO);
}
