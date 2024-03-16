package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.FileSystemItem;

import java.util.List;

public interface CustomFileSystemItemRepository {
    List<FileSystemItem> retrieveChildrenByCursor(Long parentId, Long cursorId, int numberOfResults);
}
