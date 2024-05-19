package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.QFileMetaInfo;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.CustomFileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomFileMetaInfoRepositoryImpl extends CommonRepository implements CustomFileMetaInfoRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FileMetaInfo> retrieveFileMetaInfoContainingTag(Tag tag) {
        QFileMetaInfo fileMetaInfo = QFileMetaInfo.fileMetaInfo;

        return new JPAQuery<FileMetaInfo>(entityManager)
                .select(fileMetaInfo)
                .from(fileMetaInfo)
                .where(fileMetaInfo.tagList.contains(tag))
                .fetch();
    }

}
