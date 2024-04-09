package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface BookInfoRepository extends TransactionalRepository<BookInfo, Long> {
    @Query(value = "select bi.id from BookInfo bi where bi.fileMetaInfo.id in :fileMetaInfoIdList")
    Set<Long> findAllByFileMetaInfoId(List<Long> fileMetaInfoIdList);
}
