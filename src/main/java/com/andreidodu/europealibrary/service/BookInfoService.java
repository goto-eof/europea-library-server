package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;

public interface BookInfoService {
    FileMetaInfoBookDTO retrieveById(Long id);

    FileMetaInfoBookDTO createBookInfo(FileMetaInfoBookDTO dto);

    FileMetaInfoBookDTO updateBookInfo(Long fileMetaInfoId, FileMetaInfoBookDTO dto);

    OperationStatusDTO delete(Long id);

    FileMetaInfoBookDTO retrieveByFileSystemItemId(Long fileSystemItemId);

    OperationStatusDTO bulkLanguageRename(RenameDTO renameDTO);

    OperationStatusDTO bulkPublisherRename(RenameDTO renameDTO);
}
