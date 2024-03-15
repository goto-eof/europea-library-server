package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.JobStepEnum;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.FileSystemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
public class FileSystemServiceImpl implements FileSystemService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;

    @Override
    public List<FileSystemItemDTO> readDirectory(Long id) {
        return Optional.ofNullable(id)
                .map(this::manageCaseReadDirectoryIdProvided)
                .orElse(manageCaseReadDirectoryNoIdProvided());
    }

    private List<FileSystemItemDTO> manageCaseReadDirectoryIdProvided(Long id) {
        FileSystemItem fileSystemItem = checkFileSystemItemExistence(id);
        return this.fileSystemItemMapper.toDTO(fileSystemItem.getChildrenList());
    }

    private List<FileSystemItemDTO> manageCaseReadDirectoryNoIdProvided() {
        Optional<FileSystemItem> fileSystemItemOptional = this.fileSystemItemRepository.findByLowestId(JobStepEnum.READY.getStepNumber());
        if (fileSystemItemOptional.isEmpty()) {
            throw new ApplicationException("Entity not found");
        }
        return fileSystemItemMapper.toDTO(fileSystemItemOptional.get().getChildrenList());
    }

    private FileSystemItem checkFileSystemItemExistence(Long id) {
        Optional<FileSystemItem> model = this.fileSystemItemRepository.findById(id);
        if (model.isEmpty()) {
            throw new ApplicationException("Entity not found");
        }
        return model.get();
    }
}
