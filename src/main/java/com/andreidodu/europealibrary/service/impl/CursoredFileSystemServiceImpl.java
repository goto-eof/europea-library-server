package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.CategoryMapper;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.mapper.TagMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CursoredFileSystemServiceImpl implements CursoredFileSystemService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public CursoredFileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO) {
        return Optional.ofNullable(cursorRequestDTO.getParentId())
                .map(id -> this.manageCaseReadDirectoryIdProvided(cursorRequestDTO))
                .orElse(manageCaseReadDirectoryNoIdProvided());
    }

    @Override
    public CursoredFileSystemItemDTO readDirectory() {
        return manageCaseReadDirectoryNoIdProvided();
    }

    @Override
    public CursoredCategoryDTO retrieveByCategoryId(CursorRequestDTO cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid category id"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCategoryId(cursorRequestDTO);
        CursoredCategoryDTO cursoredCategoryDTO = new CursoredCategoryDTO();
        cursoredCategoryDTO.setChildrenList(calculateChildren(children));
        cursoredCategoryDTO.setNextCursor(calculateNextCursor(children));
        this.categoryRepository.findById(cursorRequestDTO.getParentId())
                .ifPresent(category -> cursoredCategoryDTO.setCategoryDTO(this.categoryMapper.toDTO(category)));
        return cursoredCategoryDTO;
    }

    @Override
    public CursoredTagDTO retrieveByTagId(CursorRequestDTO cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid tag id"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByTagId(cursorRequestDTO);
        CursoredTagDTO cursoredTagDTO = new CursoredTagDTO();
        cursoredTagDTO.setChildrenList(calculateChildren(children));
        cursoredTagDTO.setNextCursor(calculateNextCursor(children));
        this.tagRepository.findById(cursorRequestDTO.getParentId())
                .ifPresent(tag -> cursoredTagDTO.setTag(this.tagMapper.toDTO(tag)));
        return cursoredTagDTO;
    }

    private CursoredFileSystemItemDTO manageCaseReadDirectoryIdProvided(CursorRequestDTO cursorRequestDTO) {
        CursoredFileSystemItemDTO cursoredFileSystemItemDTO = new CursoredFileSystemItemDTO();
        FileSystemItem parent = checkFileSystemItemExistence(cursorRequestDTO.getParentId());
        cursoredFileSystemItemDTO.setParent(this.fileSystemItemMapper.toDTOWithoutChildrenAndParent(parent));
        this.fileSystemItemMapper.toParentDTORecursively(cursoredFileSystemItemDTO.getParent(), parent);
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursor(cursorRequestDTO);
        cursoredFileSystemItemDTO.setChildrenList(calculateChildren(children));
        cursoredFileSystemItemDTO.setNextCursor(calculateNextCursor(children));
        return cursoredFileSystemItemDTO;
    }

    private Long calculateNextCursor(List<FileSystemItem> children) {
        if (children.size() <= ApplicationConst.MAX_ITEMS_RETRIEVE) {
            return null;
        }
        return children.get(ApplicationConst.MAX_ITEMS_RETRIEVE)
                .getId();
    }

    private List<FileSystemItemDTO> calculateChildren(List<FileSystemItem> children) {
        List<FileSystemItemDTO> childrenList = this.fileSystemItemMapper.toDTO(children);
        if (children.size() <= ApplicationConst.MAX_ITEMS_RETRIEVE) {
            return childrenList;
        }
        return childrenList.stream()
                .limit(ApplicationConst.MAX_ITEMS_RETRIEVE)
                .collect(Collectors.toList());
    }

    private CursoredFileSystemItemDTO manageCaseReadDirectoryNoIdProvided() {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findByLowestId(JobStepEnum.READY.getStepNumber())
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        CursorRequestDTO cursorRequestDTO = new CursorRequestDTO();
        cursorRequestDTO.setParentId(fileSystemItem.getId());
        cursorRequestDTO.setLimit(ApplicationConst.MAX_ITEMS_RETRIEVE);
        return manageCaseReadDirectoryIdProvided(cursorRequestDTO);
    }

    private FileSystemItem checkFileSystemItemExistence(Long id) {
        return this.fileSystemItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }
}
