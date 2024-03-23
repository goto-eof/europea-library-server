package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursoredFileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;

public interface CursoredFileSystemService {
    CursoredFileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO);

    CursoredFileSystemItemDTO readDirectory();
}
