package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursorTypeRequestDTO;
import com.andreidodu.europealibrary.dto.SearchFileSystemItemRequestDTO;
import com.andreidodu.europealibrary.model.FileExtensionProjection;
import com.andreidodu.europealibrary.model.FileSystemItem;

import java.util.List;

public interface CustomFileSystemItemRepository {
    List<FileSystemItem> retrieveChildrenByCursor(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredCategoryId(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredTagId(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCursoredFileExtension(CursorTypeRequestDTO cursorTypeRequestDTO);

    List<FileExtensionProjection> retrieveExtensionsInfo();

    List<FileSystemItem> search(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);
}
