package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.PaginatedExplorerOptions;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.QFeaturedFileMetaInfo;
import com.andreidodu.europealibrary.repository.CustomFeaturedFileSystemItemRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomFeaturedFileSystemItemRepositoryImpl extends CommonRepository implements CustomFeaturedFileSystemItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PairDTO<List<FileSystemItem>, Long> retrieveCursored(PaginatedExplorerOptions paginatedExplorerOptions, CursorCommonRequestDTO commonRequestDTO) {

        Long cursorId = commonRequestDTO.getNextCursor();
        int numberOfResults = LimitUtil.calculateLimit(commonRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFeaturedFileMetaInfo featuredFileMetaInfo = QFeaturedFileMetaInfo.featuredFileMetaInfo;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        super.applyCommonFilter(featuredFileMetaInfo.fileMetaInfo, paginatedExplorerOptions, booleanBuilder);

        if (cursorId != null) {
            booleanBuilder.and(featuredFileMetaInfo.id.loe(cursorId));
        }

        List<FileMetaInfo> featuredFileMetaInfoList = new JPAQuery<FileMetaInfo>(entityManager)
                .select(featuredFileMetaInfo.fileMetaInfo)
                .from(featuredFileMetaInfo)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(featuredFileMetaInfo.id.desc())
                .fetch();

        Long nextId = featuredFileMetaInfoList.stream()
                .skip(numberOfResults)
                .limit(1)
                .findFirst()
                .map(item -> item.getFeaturedFileMetaInfo().getId())
                .orElse(null);

        return new PairDTO<List<FileSystemItem>, Long>(featuredFileMetaInfoList
                .stream()
                .map(featured ->
                        featured.getFileSystemItemList()
                                .stream()
                                .filter(fsi -> JobStepEnum.READY.getStepNumber() == fsi.getJobStep())
                                .limit(1).findFirst().get()
                ).limit(numberOfResults)
                .toList(), nextId);
    }
}
