package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursoredCategoryDTO;
import com.andreidodu.europealibrary.dto.CursoredFileSystemItemDTO;
import com.andreidodu.europealibrary.dto.CursoredTagDTO;

public interface CursoredFileSystemService {
    CursoredFileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO);

    CursoredFileSystemItemDTO readDirectory();

    CursoredCategoryDTO retrieveByCategoryId(CursorRequestDTO cursorRequestDTO);

    CursoredTagDTO retrieveByTagId(CursorRequestDTO cursorRequestDTO);
}
