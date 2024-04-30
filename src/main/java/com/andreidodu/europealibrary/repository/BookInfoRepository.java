package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface BookInfoRepository extends TransactionalRepository<BookInfo, Long>, CustomBookInfoRepository {
    @Query(value = "select bi.id from BookInfo bi where bi.fileMetaInfo.id in :fileMetaInfoIdList")
    Set<Long> findAllByFileMetaInfoId(List<Long> fileMetaInfoIdList);

    @Modifying
    @Query("update BookInfo bi set bi.language = :newName where bi.language = :oldName")
    int renameLanguage(String oldName, String newName);

    @Modifying
    @Query("update BookInfo bi set bi.publisher = :newName where bi.publisher = :oldName")
    int renamePublisher(String oldName, String newName);

    @Modifying
    @Query("update BookInfo bi set bi.publishedDate = :newName where bi.publishedDate = :oldName")
    int renamePublishedDate(String oldName, String newName);
}
