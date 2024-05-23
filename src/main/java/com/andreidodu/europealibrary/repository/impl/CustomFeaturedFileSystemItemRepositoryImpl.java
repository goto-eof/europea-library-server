package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.QFeaturedFileMetaInfo;
import com.andreidodu.europealibrary.model.QFileSystemItem;
import com.andreidodu.europealibrary.repository.CustomFeaturedFileSystemItemRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomFeaturedFileSystemItemRepositoryImpl extends CommonRepository implements CustomFeaturedFileSystemItemRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FileSystemItem> retrieveCursored(CursorCommonRequestDTO commonRequestDTO) {

        Long cursorId = commonRequestDTO.getNextCursor();
        int numberOfResults = LimitUtil.calculateLimit(commonRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QFeaturedFileMetaInfo featuredFileSystemItem = QFeaturedFileMetaInfo.featuredFileMetaInfo;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        booleanBuilder.and(featuredFileSystemItem.fileMetaInfo.id.eq(fileSystemItem.fileMetaInfo.id));
        if (cursorId != null) {
            booleanBuilder.and(featuredFileSystemItem.id.goe(cursorId));
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(featuredFileSystemItem, fileSystemItem)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(featuredFileSystemItem.createdDate.desc())
                .fetch();
    }
}
