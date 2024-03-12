package com.andreidodu.europalibrary.service.impl;

import com.andreidodu.europalibrary.dto.FileDTO;
import com.andreidodu.europalibrary.mapper.FileMapper;
import com.andreidodu.europalibrary.repository.FileRepository;
import com.andreidodu.europalibrary.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    public List<FileDTO> getFilesInDirectoryPaginated(final String path, final int pageNumber) {
        return this.fileRepository.getFilesInDirectoryPaginated(path, pageNumber)
                .stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
}
