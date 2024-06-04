package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.*;
import org.springframework.security.core.Authentication;

public interface FeaturedFileSystemItemService {

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursored(Authentication authentication, CursorCommonRequestDTO cursorCommonRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredHighlight(Authentication authentication, CursorCommonRequestDTO cursorCommonRequestDTO);

    OperationStatusDTO addFeatured(Long fileSystemItemId);

    OperationStatusDTO removeFeatured(Long fileSystemItemId);

    OperationStatusDTO isFeatured(Long fileSystemItemId);

}
