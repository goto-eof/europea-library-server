package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.*;

import java.util.List;

public interface CursoredFileSystemService {
    CursoredFileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO);

    CursoredFileSystemItemDTO readDirectory();

    CursoredCategoryDTO retrieveByCategoryId(CursorRequestDTO cursorRequestDTO);

    CursoredTagDTO retrieveByTagId(CursorRequestDTO cursorRequestDTO);

    List<FileExtensionDTO> getAllExtensions();

    CursoredFileExtensionDTO retrieveByFileExtension(CursorTypeRequestDTO cursorTypeRequestDTO);

    DownloadDTO retrieveResourceForDownload(Long fileSystemId);

    FileSystemItemDTO get(Long fileSystemItemId);
}
