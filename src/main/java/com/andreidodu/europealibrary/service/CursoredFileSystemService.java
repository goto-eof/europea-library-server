package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;

public interface CursoredFileSystemService {
    FileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO);

    FileSystemItemDTO readDirectory();
}
