package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.model.FileExtensionProjection;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.ItemAndFrequencyProjection;

import java.util.List;
import java.util.Optional;

public interface CustomFileSystemItemRepository {
    List<FileSystemItem> retrieveChildrenByCursor(PaginatedExplorerOptions paginatedExplorerOptions, CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredCategoryId(PaginatedExplorerOptions paginatedExplorerOptions, CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredTagId(PaginatedExplorerOptions paginatedExplorerOptions, CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredFileExtension(PaginatedExplorerOptions paginatedExplorerOptions, CursorTypeRequestDTO cursorTypeRequestDTO);

    List<FileExtensionProjection> retrieveExtensionsInfo(PaginatedExplorerOptions paginatedExplorerOptions);

    List<ItemAndFrequencyProjection> retrievePublishersInfo(PaginatedExplorerOptions paginatedExplorerOptions);

    List<FileSystemItem> search(PaginatedExplorerOptions paginatedExplorerOptions, SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);

    List<ItemAndFrequencyProjection> retrieveLanguagesInfo(PaginatedExplorerOptions paginatedExplorerOptions);

    List<FileSystemItem> retrieveChildrenByCursoredLanguage(PaginatedExplorerOptions paginatedExplorerOptions, GenericCursorRequestDTO<String> cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredPublisher(PaginatedExplorerOptions paginatedExplorerOptions, GenericCursorRequestDTO<String> cursorRequestDTO);

    List<ItemAndFrequencyProjection> retrievePublishedDatesInfo(PaginatedExplorerOptions paginatedExplorerOptions);

    List<FileSystemItem> retrieveChildrenByCursoredPublishedDate(PaginatedExplorerOptions paginatedExplorerOptions, GenericCursorRequestDTO<String> cursorRequestDTO);

    PairDTO<List<PairDTO<FileSystemItem, PairDTO<Double, Long>>>, Long> retrieveChildrenByCursoredRating(PaginatedExplorerOptions paginatedExplorerOptions, CursorCommonRequestDTO cursorRequestDTO);

    PairDTO<List<FileSystemItem>, Long> retrieveCursoredByDownloadCount(PaginatedExplorerOptions paginatedExplorerOptions, CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    List<FileSystemItem> retrieveNewCursored(PaginatedExplorerOptions paginatedExplorerOptions, CursorCommonRequestDTO cursorRequestDTO);

    PairDTO<List<PairDTO<FileSystemItem, Long>>, Long> retrieveCursoredByTopSold(PaginatedExplorerOptions paginatedExplorerOptions, CommonCursoredRequestDTO commonCursoredRequestDTO);
}
