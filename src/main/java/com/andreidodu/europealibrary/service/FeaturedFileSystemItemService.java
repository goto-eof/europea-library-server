package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.*;

public interface FeaturedFileSystemItemService {

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursored(CursorCommonRequestDTO cursorCommonRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredHighlight(CursorCommonRequestDTO cursorCommonRequestDTO);

    OperationStatusDTO addFeatured(Long fileSystemItemId);

    OperationStatusDTO removeFeatured(Long fileSystemItemId);

    OperationStatusDTO isFeatured(Long fileSystemItemId);

}
