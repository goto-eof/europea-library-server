package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;

import java.util.List;

public interface FileSystemService {
    List<FileSystemItemDTO> readDirectory(Long id);
}
