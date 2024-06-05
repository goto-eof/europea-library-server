package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CursoredFileSystemService {
    CursoredFileSystemItemDTO readDirectory(Authentication authentication, CursorRequestDTO cursorRequestDTO);

    CursoredFileSystemItemDTO readDirectory(Authentication authentication);

    CursoredCategoryDTO retrieveByCategoryId(Authentication authentication, CursorRequestDTO cursorRequestDTO);

    CursoredTagDTO retrieveByTagId(Authentication authentication, CursorRequestDTO cursorRequestDTO);

    List<FileExtensionDTO> getAllExtensions(Authentication authentication);

    CursoredFileExtensionDTO retrieveByFileExtension(Authentication authentication, CursorTypeRequestDTO cursorTypeRequestDTO);

    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_LANGUAGES})
    List<ItemAndFrequencyDTO> retrieveAllLanguages(Authentication authentication);

    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_PUBLISHERS})
    List<ItemAndFrequencyDTO> retrieveAllPublishers(Authentication authentication);

    DownloadDTO retrieveResourceForDownload(Authentication authentication, Long fileSystemId);

    FileSystemItemDTO get(Authentication authentication, Long fileSystemItemId);

    SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO> search(Authentication authentication, SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByLanguage(Authentication authentication, GenericCursorRequestDTO<String> cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByPublisher(Authentication authentication, GenericCursorRequestDTO<String> cursorRequestDTO);

    List<ItemAndFrequencyDTO> retrieveAllPublishedDates(Authentication authentication);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveByPublishedDate(Authentication authentication, GenericCursorRequestDTO<String> cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursoredByRating(Authentication authentication, CursorCommonRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursoredByDownloadCount(Authentication authentication, CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveNewCursored(Authentication authentication, CursorCommonRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveNewCursoredHighlight(Authentication authentication, CursorCommonRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredByDownloadCountHighlight(Authentication authentication, CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    FileSystemItemDTO getByFileMetaInfoId(Authentication authentication, Long fileMetaInfoId);
}
