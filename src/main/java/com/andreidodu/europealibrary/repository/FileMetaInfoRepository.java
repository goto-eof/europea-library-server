package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileMetaInfoRepository extends TransactionalRepository<FileMetaInfo, Long> , CustomFileMetaInfoRepository{

    @Query("select fmi from FileMetaInfo fmi where fmi.bookInfo.isbn10 = :isbn or fmi.bookInfo.isbn13 = :isbn")
    List<FileMetaInfo> findByIsbn(String isbn);

    @Query("select fmi from FileMetaInfo fmi where fmi.bookInfo.isbn10 in (:isbnList) or fmi.bookInfo.isbn13 in (:isbnList)")
    List<FileMetaInfo> findByIsbnList(List<String> isbnList);
}
