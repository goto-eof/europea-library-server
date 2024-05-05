package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.Tag;

import java.util.List;

public interface CustomFeaturedFileSystemItemRepository {
    List<FileSystemItem> retrieveCursored(CursorCommonRequestDTO commonRequestDTO);
}
