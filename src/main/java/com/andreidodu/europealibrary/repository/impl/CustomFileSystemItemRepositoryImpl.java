package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.QFileSystemItem;
import com.andreidodu.europealibrary.repository.CustomFileSystemItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class CustomFileSystemItemRepositoryImpl implements CustomFileSystemItemRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FileSystemItem> retrieveChildrenByCursor(Long parentId, Long cursorId, int numberOfResults) {
        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.parent.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.MAX_ITEMS_RETRIEVEMENT) {
            numberOfResults = ApplicationConst.MAX_ITEMS_RETRIEVEMENT;
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .fetch();
    }
}
