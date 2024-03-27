package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;

import java.util.List;

public interface CustomFileSystemItemRepository {
    List<FileSystemItem> retrieveChildrenByCursor(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByCategoryId(CursorRequestDTO cursorRequestDTO);

    List<FileSystemItem> retrieveChildrenByTagId(CursorRequestDTO cursorRequestDTO);
}
