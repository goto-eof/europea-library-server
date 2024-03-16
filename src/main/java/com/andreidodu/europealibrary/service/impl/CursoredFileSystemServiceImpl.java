package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.JobStepEnum;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import com.andreidodu.europealibrary.service.FileSystemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CursoredFileSystemServiceImpl implements CursoredFileSystemService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;

    @Override
    public FileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO) {
        return Optional.ofNullable(cursorRequestDTO.getNextCursor())
                .map(id -> this.manageCaseReadDirectoryIdProvided(cursorRequestDTO))
                .orElse(manageCaseReadDirectoryNoIdProvided());
    }

    @Override
    public FileSystemItemDTO readDirectory() {
        return manageCaseReadDirectoryNoIdProvided();
    }

    private FileSystemItemDTO manageCaseReadDirectoryIdProvided(CursorRequestDTO cursorRequestDTO) {
        FileSystemItem parent = checkFileSystemItemExistence(cursorRequestDTO.getParentId());
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursor(cursorRequestDTO.getParentId(), cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit());
        FileSystemItemDTO parentDTO = this.fileSystemItemMapper.toDTOParent(parent);
        parentDTO.setChildrenList(this.fileSystemItemMapper.toDTO(children));
        return parentDTO;
    }

    private FileSystemItemDTO manageCaseReadDirectoryNoIdProvided() {
        Optional<FileSystemItem> fileSystemItemOptional = this.fileSystemItemRepository.findByLowestId(JobStepEnum.READY.getStepNumber());
        if (fileSystemItemOptional.isEmpty()) {
            throw new ApplicationException("Entity not found");
        }
        FileSystemItem fileSystemItem = fileSystemItemOptional.get();
        CursorRequestDTO cursorRequestDTO = new CursorRequestDTO();
        cursorRequestDTO.setParentId(fileSystemItem.getId());
        cursorRequestDTO.setLimit(10);
        return manageCaseReadDirectoryIdProvided(cursorRequestDTO);
    }

    private FileSystemItem checkFileSystemItemExistence(Long id) {
        Optional<FileSystemItem> model = this.fileSystemItemRepository.findById(id);
        if (model.isEmpty()) {
            throw new ApplicationException("Entity not found");
        }
        return model.get();
    }
}
