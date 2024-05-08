package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.model.FileExtensionProjection;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.ItemAndFrequencyProjection;

import java.util.List;
import java.util.Optional;

public interface CustomFileSystemItemRepository {
    List<FileSystemItem> retrieveChildrenByCursor(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredCategoryId(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredTagId(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredFileExtension(CursorTypeRequestDTO cursorTypeRequestDTO);

    List<FileExtensionProjection> retrieveExtensionsInfo();

    List<ItemAndFrequencyProjection> retrievePublishersInfo();

    List<FileSystemItem> search(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);

    List<ItemAndFrequencyProjection> retrieveLanguagesInfo();

    List<FileSystemItem> retrieveChildrenByCursoredLanguage(GenericCursorRequestDTO<String> cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredPublisher(GenericCursorRequestDTO<String> cursorRequestDTO);

    List<ItemAndFrequencyProjection> retrievePublishedDatesInfo();

    List<FileSystemItem> retrieveChildrenByCursoredPublishedDate(GenericCursorRequestDTO<String> cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredRating(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveCursoredByDownloadCount(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    List<FileSystemItem> retrieveNewCursored(CursorCommonRequestDTO cursorRequestDTO);
}
