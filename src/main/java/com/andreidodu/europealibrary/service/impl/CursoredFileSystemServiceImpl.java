package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.constants.PersistenceConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.*;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
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
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredCategoryDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredCategoryDTO::setNextCursor);
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
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
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
        cursoredFileSystemItemDTO.setChildrenList(this.fileSystemItemMapper.toDTO(limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredFileSystemItemDTO::setNextCursor);
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
        List<FileSystemItem> childrenList = limit(children, cursorTypeRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredFileExtensionDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorTypeRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(cursoredFileExtensionDTO::setNextCursor);
        cursoredFileExtensionDTO.setExtension(cursorTypeRequestDTO.getExtension());
        return cursoredFileExtensionDTO;
    }

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_LANGUAGES})
    @Transactional(timeout = PersistenceConst.TIMEOUT_DEMANDING_QUERIES_SECONDS, propagation = Propagation.REQUIRED)
    public List<ItemAndFrequencyDTO> retrieveAllLanguages() {
        return this.itemAndFrequencyMapper.toDTO(this.fileSystemItemRepository.retrieveLanguagesInfo());
    }


    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_PUBLISHERS})
    @Transactional(timeout = PersistenceConst.TIMEOUT_DEMANDING_QUERIES_SECONDS, propagation = Propagation.REQUIRED)
    public List<ItemAndFrequencyDTO> retrieveAllPublishers() {
        return this.itemAndFrequencyMapper.toDTO(this.fileSystemItemRepository.retrievePublishersInfo());
    }

    @Override
    public DownloadDTO retrieveResourceForDownload(Long fileSystemId) {
        return this.fileSystemItemRepository.findById(fileSystemId)
                .map(fileSystemItem -> {
                    try {
                        fileSystemItem.setDownloadCount(fileSystemItem.getDownloadCount() + 1);
                        this.fileSystemItemRepository.save(fileSystemItem);
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
        result.setChildrenList(this.fileSystemItemMapper.toDTO(limit(children, searchFileSystemItemRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)));
        super.calculateNextId(children, searchFileSystemItemRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(result::setNextCursor);
        result.setQuery(searchFileSystemItemRequestDTO);
        return result;
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByLanguage(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParent())
                .orElseThrow(() -> new EntityNotFoundException("Invalid parent"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredLanguage(cursorRequestDTO);
        GenericCursoredResponseDTO<String, FileSystemItemDTO> cursoredTagDTO = new GenericCursoredResponseDTO<>();
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
        cursoredTagDTO.setParent(cursorRequestDTO.getParent());
        return cursoredTagDTO;
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByPublisher(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParent())
                .orElseThrow(() -> new EntityNotFoundException("Invalid parent"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredPublisher(cursorRequestDTO);
        GenericCursoredResponseDTO<String, FileSystemItemDTO> cursoredTagDTO = new GenericCursoredResponseDTO<>();
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
        cursoredTagDTO.setParent(cursorRequestDTO.getParent());
        return cursoredTagDTO;
    }

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_PUBLISHED_DATES})
    @Transactional(timeout = PersistenceConst.TIMEOUT_DEMANDING_QUERIES_SECONDS, propagation = Propagation.REQUIRED)
    public List<ItemAndFrequencyDTO> retrieveAllPublishedDates() {
        return this.itemAndFrequencyMapper.toDTO(this.fileSystemItemRepository.retrievePublishedDatesInfo());
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByPublishedDate(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Optional.ofNullable(cursorRequestDTO.getParent())
                .orElseThrow(() -> new EntityNotFoundException("Invalid parent"));
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredPublishedDate(cursorRequestDTO);
        GenericCursoredResponseDTO<String, FileSystemItemDTO> cursoredTagDTO = new GenericCursoredResponseDTO<>();
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE).ifPresent(cursoredTagDTO::setNextCursor);
        cursoredTagDTO.setParent(cursorRequestDTO.getParent());
        return cursoredTagDTO;
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> readDirectoryByRating(CursorRequestDTO cursorRequestDTO) {
        return Optional.ofNullable(cursorRequestDTO.getParentId())
                .map(id -> this.manageCaseListByRatingIdProvided(cursorRequestDTO))
                .orElse(manageCaseListByRatingNoIdProvided());
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursoredByDownloadCount(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {
        return this.genericRetrieveCursoredByDownloadCount(cursoredRequestByFileTypeDTO, fileSystemItemMapper::toDTOWithParentDTORecursively);
    }

    private <T> GenericCursoredResponseDTO<String, T> genericRetrieveCursoredByDownloadCount(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO, Function<FileSystemItem, T> toDTO) {
        GenericCursoredResponseDTO<String, T> responseDTO = new GenericCursoredResponseDTO<>();
        responseDTO.setParent("DownloadCount");
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveCursoredByDownloadCount(cursoredRequestByFileTypeDTO);
        List<FileSystemItem> childrenList = limit(children, cursoredRequestByFileTypeDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        responseDTO.setChildrenList(childrenList.stream()
                .map(toDTO)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursoredRequestByFileTypeDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(responseDTO::setNextCursor);
        return responseDTO;
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredByDownloadCountHighlight(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {
        return this.genericRetrieveCursoredByDownloadCount(cursoredRequestByFileTypeDTO, fileSystemItemMapper::toHighlightDTO);
    }

    public <T> GenericCursoredResponseDTO<String, FileSystemItemDTO> genericRetrieveCursoredByDownloadCount(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {
        GenericCursoredResponseDTO<String, FileSystemItemDTO> responseDTO = new GenericCursoredResponseDTO<>();
        responseDTO.setParent("DownloadCount");
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveCursoredByDownloadCount(cursoredRequestByFileTypeDTO);
        List<FileSystemItem> childrenList = limit(children, cursoredRequestByFileTypeDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        responseDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursoredRequestByFileTypeDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(responseDTO::setNextCursor);
        return responseDTO;
    }

    private GenericCursoredResponseDTO<String, FileSystemItemDTO> manageCaseListByRatingIdProvided(CursorRequestDTO cursorRequestDTO) {
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveChildrenByCursoredRating(cursorRequestDTO);
        GenericCursoredResponseDTO<String, FileSystemItemDTO> cursoredTagDTO = new GenericCursoredResponseDTO<>();
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredTagDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(cursoredTagDTO::setNextCursor);
        cursoredTagDTO.setParent("by rating");
        return cursoredTagDTO;
    }

    private GenericCursoredResponseDTO<String, FileSystemItemDTO> manageCaseListByRatingNoIdProvided() {
        var cursoredRequest = new CursorRequestDTO();
        cursoredRequest.setLimit(ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        return this.manageCaseListByRatingIdProvided(cursoredRequest);
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveNewCursored(CursorCommonRequestDTO cursorRequestDTO) {
        return this.genericRetrieveNewCursored(cursorRequestDTO, this.fileSystemItemMapper::toDTOWithParentDTORecursively);
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveNewCursoredHighlight(CursorCommonRequestDTO cursorRequestDTO) {
        return this.genericRetrieveNewCursored(cursorRequestDTO, this.fileSystemItemMapper::toHighlightDTO);
    }

    public <T> GenericCursoredResponseDTO<String, T> genericRetrieveNewCursored(CursorCommonRequestDTO cursorRequestDTO, Function<FileSystemItem, T> toDTO) {
        GenericCursoredResponseDTO<String, T> responseDTO = new GenericCursoredResponseDTO<>();
        responseDTO.setParent("New");
        List<FileSystemItem> children = this.fileSystemItemRepository.retrieveNewCursored(cursorRequestDTO);
        List<FileSystemItem> childrenList = limit(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        responseDTO.setChildrenList(childrenList.stream()
                .map(toDTO)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(responseDTO::setNextCursor);
        return responseDTO;
    }

}
