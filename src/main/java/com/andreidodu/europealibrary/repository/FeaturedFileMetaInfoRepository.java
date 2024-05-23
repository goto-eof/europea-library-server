package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FeaturedFileMetaInfo;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface FeaturedFileMetaInfoRepository extends TransactionalRepository<FeaturedFileMetaInfo, Long>, CustomFeaturedFileSystemItemRepository {
}
