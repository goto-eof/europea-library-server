package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface BookInfoRepository extends TransactionalRepository<BookInfo, Long> {
}
