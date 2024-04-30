package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.Tag;

import java.util.List;

public interface CustomFileMetaInfoRepository {
    List<FileMetaInfo> retrieveFileMetaInfoContainingTag(Tag tag);

}
