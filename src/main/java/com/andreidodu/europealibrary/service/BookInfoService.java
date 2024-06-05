package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.stripe.exception.StripeException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface BookInfoService {
//    FileMetaInfoBookDTO retrieveById(Long id);

//    FileMetaInfoBookDTO createBookInfo(FileMetaInfoBookDTO dto);

//    FileMetaInfoBookDTO updateBookInfo(Long fileMetaInfoId, FileMetaInfoBookDTO dto);

    OperationStatusDTO delete(Long id);

//    FileMetaInfoBookDTO retrieveByFileSystemItemId(Long fileSystemItemId);

    OperationStatusDTO bulkLanguageRename(Authentication authentication, RenameDTO renameDTO);

    OperationStatusDTO bulkPublisherRename(Authentication authentication, RenameDTO renameDTO);

    OperationStatusDTO bulkPublishedDateRename(Authentication authentication, RenameDTO renameDTO);

    OperationStatusDTO lock(Long fileMetaInfoId);

    OperationStatusDTO unlock(Long fileMetaInfoId);

    OperationStatusDTO uploadBookCover(Long metaInfoId, MultipartFile file);

    FileMetaInfoDTO updateFileMetaInfoFull(Long fileMetaInfoId, FileMetaInfoDTO dto) throws StripeException;
}
