package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.*;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface CursoredFileSystemService {
    CursoredFileSystemItemDTO readDirectory(CursorRequestDTO cursorRequestDTO);

    CursoredFileSystemItemDTO readDirectory();

    CursoredCategoryDTO retrieveByCategoryId(CursorRequestDTO cursorRequestDTO);

    CursoredTagDTO retrieveByTagId(CursorRequestDTO cursorRequestDTO);

    List<FileExtensionDTO> getAllExtensions();

    CursoredFileExtensionDTO retrieveByFileExtension(CursorTypeRequestDTO cursorTypeRequestDTO);

    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_LANGUAGES})
    List<ItemAndFrequencyDTO> retrieveAllLanguages();

    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_PUBLISHERS})
    List<ItemAndFrequencyDTO> retrieveAllPublishers();

    DownloadDTO retrieveResourceForDownload(Long fileSystemId);

    FileSystemItemDTO get(Long fileSystemItemId);

    SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO> search(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByLanguage(GenericCursorRequestDTO<String> cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByPublisher(GenericCursorRequestDTO<String> cursorRequestDTO);

    List<ItemAndFrequencyDTO> retrieveAllPublishedDates();

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByPublishedDate(GenericCursorRequestDTO<String> cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> readDirectoryByRating(CursorRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursoredByDownloadCount(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveNewCursored(CursorCommonRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveNewCursoredHighlight(CursorCommonRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredByDownloadCountHighlight(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    FileSystemItemDTO getByFileMetaInfoId(Long fileMetaInfoId);
}
