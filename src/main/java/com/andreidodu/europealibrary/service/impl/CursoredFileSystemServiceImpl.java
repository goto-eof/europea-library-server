package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.*;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CursoredFileSystemServiceImpl extends CursoredServiceCommon implements CursoredFileSystemService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final ItemAndFrequencyMapper itemAndFrequencyMapper;
    private final FileSystemItemMapper fileSystemItemMapper;
    private final FileExtensionMapper fileExtensionMapper;
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
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredCategoryId(cursorRequestDTO);
        CursoredCategoryDTO cursoredCategoryDTO = new CursoredCategoryDTO();
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredCategoryDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredCategoryDTO::setNextCursor);
        this.categoryRepository.findById(cursorRequestDTO.getParentId())
                .ifPresent(category -> cursoredCategoryDTO.setCategory(this.categoryMapper.toDTO(category)));
        return cursoredCategoryDTO;
    }

    @Override
    public CursoredTagDTO retrieveByTagId(CursorRequestDTO cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid tag id"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredTagId(cursorRequestDTO);
        CursoredTagDTO cursoredTagDTO = new CursoredTagDTO();
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
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
        cursoredFileSystemItemDTO.setChildrenList(this.fileSystemItemMapper.toDTO(limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredFileSystemItemDTO::setNextCursor);
        return cursoredFileSystemItemDTO;
    }


    private CursoredFileSystemItemDTO manageCaseReadDirectoryNoIdProvided() {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findByNoParent(JobStepEnum.READY.getStepNumber())
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        CursorRequestDTO cursorRequestDTO = new CursorRequestDTO();
        cursorRequestDTO.setParentId(fileSystemItem.getId());
        cursorRequestDTO.setLimit(ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        return manageCaseReadDirectoryIdProvided(cursorRequestDTO);
    }

    private FileSystemItem checkFileSystemItemExistence(Long id) {
        return this.fileSystemItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_EXTENSIONS})
    public List<FileExtensionDTO> getAllExtensions() {
        return this.fileExtensionMapper.toDTO(this.fileSystemItemRepository.retrieveExtensionsInfo());
    }

    @Override
    public CursoredFileExtensionDTO retrieveByFileExtension(CursorTypeRequestDTO cursorTypeRequestDTO) {
        Optional.ofNullable(cursorTypeRequestDTO.getExtension())
                .orElseThrow(() -> new EntityNotFoundException("Invalid file extension"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredFileExtension(cursorTypeRequestDTO);
        CursoredFileExtensionDTO cursoredFileExtensionDTO = new CursoredFileExtensionDTO();
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredFileExtensionDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(cursoredFileExtensionDTO::setNextCursor);
        cursoredFileExtensionDTO.setExtension(cursorTypeRequestDTO.getExtension());
        return cursoredFileExtensionDTO;
    }

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_LANGUAGES})
    public List<ItemAndFrequencyDTO> retrieveAllLanguages() {
        return this.itemAndFrequencyMapper.toDTO(this.fileSystemItemRepository.retrieveLanguagesInfo());
    }


    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_PUBLISHERS})
    public List<ItemAndFrequencyDTO> retrieveAllPublishers() {
        return this.itemAndFrequencyMapper.toDTO(this.fileSystemItemRepository.retrievePublishersInfo());
    }

    @Override
    public DownloadDTO retrieveResourceForDownload(Long fileSystemId) {
        return this.fileSystemItemRepository.findById(fileSystemId)
                .map(fileSystemItem -> {
                    try {
                        File file = new File(fileSystemItem.getBasePath() + "/" + fileSystemItem.getName());
                        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                        DownloadDTO downloadDTO = new DownloadDTO();
                        downloadDTO.setInputStreamResource(resource);
                        downloadDTO.setFileSize(file.length());
                        downloadDTO.setFileName(fileSystemItem.getName());
                        return downloadDTO;
                    } catch (IOException e) {
                        throw new ApplicationException("Unable to retrieve file");
                    }
                }).orElseThrow();
    }

    @Override
    public FileSystemItemDTO get(Long fileSystemItemId) {
        return this.fileSystemItemRepository.findById(fileSystemItemId)
                .map(this.fileSystemItemMapper::toDTO)
                .orElseThrow(() -> new ApplicationException("Item not found"));
    }

    @Override
    public SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO> search(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {
        SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO> result = new SearchResultDTO<>();
        List<FileSystemItem> children = this.fileSystemItemRepository.search(searchFileSystemItemRequestDTO);
        result.setChildrenList(this.fileSystemItemMapper.toDTO(limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(result::setNextCursor);
        result.setQuery(searchFileSystemItemRequestDTO);
        return result;
    }

    @Override
    public GenericCursoredResponseDTO<String> retrieveByLanguage(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParent())
                .orElseThrow(() -> new EntityNotFoundException("Invalid parent"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredLanguage(cursorRequestDTO);
        GenericCursoredResponseDTO<String> cursoredTagDTO = new GenericCursoredResponseDTO<String>();
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
        cursoredTagDTO.setParent(cursorRequestDTO.getParent());
        return cursoredTagDTO;
    }

    @Override
    public GenericCursoredResponseDTO<String> retrieveByPublisher(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParent())
                .orElseThrow(() -> new EntityNotFoundException("Invalid parent"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredPublisher(cursorRequestDTO);
        GenericCursoredResponseDTO<String> cursoredTagDTO = new GenericCursoredResponseDTO<String>();
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
        cursoredTagDTO.setParent(cursorRequestDTO.getParent());
        return cursoredTagDTO;
    }
}
