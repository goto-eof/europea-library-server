package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;

public interface BookInfoService {
    FileMetaInfoBookDTO retrieveById(Long id);

    FileMetaInfoBookDTO createBookInfo(FileMetaInfoBookDTO dto);

    FileMetaInfoBookDTO updateBookInfo(Long fileMetaInfoId, FileMetaInfoBookDTO dto);

    OperationStatusDTO delete(Long id);

    FileMetaInfoBookDTO retrieveByFileSystemItemId(Long fileSystemItemId);
}
