package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.JobStepEnum;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.FileSystemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FileSystemServiceImpl implements FileSystemService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;

    @Override
    public FileSystemItemDTO readDirectory(Long id) {
        return Optional.ofNullable(id)
                .map(this::manageCaseReadDirectoryIdProvided)
                .orElse(manageCaseReadDirectoryNoIdProvided());
    }

    @Override
    public FileSystemItemDTO readDirectory() {
        return manageCaseReadDirectoryNoIdProvided();
    }

    private FileSystemItemDTO manageCaseReadDirectoryIdProvided(Long id) {
        FileSystemItem fileSystemItem = checkFileSystemItemExistence(id);
        fileSystemItem.getChildrenList()
                .sort(Comparator.comparing(FileSystemItem::getIsDirectory)
                        .reversed()
                        .thenComparing(FileSystemItem::getName));
        return this.fileSystemItemMapper.toDTO(fileSystemItem);
    }

    private FileSystemItemDTO manageCaseReadDirectoryNoIdProvided() {
        Optional<FileSystemItem> fileSystemItemOptional = this.fileSystemItemRepository.findByLowestId(JobStepEnum.READY.getStepNumber());
        if (fileSystemItemOptional.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        return this.manageCaseReadDirectoryIdProvided(fileSystemItemOptional.get().getId());
    }

    private FileSystemItem checkFileSystemItemExistence(Long id) {
        Optional<FileSystemItem> model = this.fileSystemItemRepository.findById(id);
        if (model.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        return model.get();
    }
}
