package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import org.springframework.data.util.Pair;

import java.util.List;

public interface CustomFeaturedFileSystemItemRepository {
    PairDTO<List<FileSystemItem>, Long> retrieveCursored(CursorCommonRequestDTO commonRequestDTO);
}
