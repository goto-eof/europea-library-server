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

    GenericCursoredResponseDTO<String> retrieveByLanguage(GenericCursorRequestDTO<String> cursorRequestDTO);

    GenericCursoredResponseDTO<String> retrieveByPublisher(GenericCursorRequestDTO<String> cursorRequestDTO);

    List<ItemAndFrequencyDTO> retrieveAllPublishedDates();

    GenericCursoredResponseDTO<String> retrieveByPublishedDate(GenericCursorRequestDTO<String> cursorRequestDTO);

    GenericCursoredResponseDTO<String>  readDirectoryByRating(CursorRequestDTO cursorRequestDTO);

    GenericCursoredResponseDTO<String> retrieveCursoredByDownloadCount(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
