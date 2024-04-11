package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;

@Deprecated
public interface FileSystemService {
    FileSystemItemDTO readDirectory(Long id);

    FileSystemItemDTO readDirectory();


}
