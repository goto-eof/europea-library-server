package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.exception.WorkInProgressException;
import com.andreidodu.europealibrary.mapper.FileSystemItemFullMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.FileSystemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Deprecated
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FileSystemServiceImpl implements FileSystemService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemFullMapper fileSystemItemFullMapper;

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
        return this.fileSystemItemFullMapper.toDTOFull(fileSystemItem);
    }

    private FileSystemItemDTO manageCaseReadDirectoryNoIdProvided() {
        return this.fileSystemItemRepository.findByNoParent(JobStepEnum.READY.getStepNumber())
                .map(item -> this.manageCaseReadDirectoryIdProvided(item.getId()))
                .orElseThrow(() -> new WorkInProgressException("Job in progress. Please retry in few minutes"));
    }

    private FileSystemItem checkFileSystemItemExistence(Long id) {
        return this.fileSystemItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }
}
