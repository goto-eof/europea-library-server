package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import org.springframework.web.multipart.MultipartFile;

public interface BookInfoService {
    FileMetaInfoBookDTO retrieveById(Long id);

    FileMetaInfoBookDTO createBookInfo(FileMetaInfoBookDTO dto);

    FileMetaInfoBookDTO updateBookInfo(Long fileMetaInfoId, FileMetaInfoBookDTO dto);

    OperationStatusDTO delete(Long id);

    FileMetaInfoBookDTO retrieveByFileSystemItemId(Long fileSystemItemId);

    OperationStatusDTO bulkLanguageRename(RenameDTO renameDTO);

    OperationStatusDTO bulkPublisherRename(RenameDTO renameDTO);

    OperationStatusDTO bulkPublishedDateRename(RenameDTO renameDTO);

    OperationStatusDTO lock(Long fileMetaInfoId);

    OperationStatusDTO unlock(Long fileMetaInfoId);

    OperationStatusDTO uploadBookCover(Long metaInfoId, MultipartFile file);
}
