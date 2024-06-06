package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.model.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface TagService {
    CursorDTO<TagDTO> retrieveAllTags(Authentication authentication, CommonCursoredRequestDTO commonCursoredRequestDTO);

    Tag loadOrCreateTagEntity(String tagName);

    OperationStatusDTO bulkRename(RenameDTO renameDTO);
}
