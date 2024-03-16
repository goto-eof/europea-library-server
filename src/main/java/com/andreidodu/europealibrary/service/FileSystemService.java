package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;

public interface FileSystemService {
    FileSystemItemDTO readDirectory(Long id);
    FileSystemItemDTO readDirectory();


}
