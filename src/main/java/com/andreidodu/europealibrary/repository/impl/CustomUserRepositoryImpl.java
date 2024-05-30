package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.model.FileSystemItemTopDownloadsView;
import com.andreidodu.europealibrary.model.security.QUser;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.util.CursoredUtil;
import com.andreidodu.europealibrary.repository.CustomUserRepository;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PairDTO<List<User>, Long> retrieveAllCursored(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        int numberOfResults = LimitUtil.calculateLimit(commonCursoredRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QUser user = QUser.user;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        Long startRowNumber = CursoredUtil.calculateRowNumber(commonCursoredRequestDTO.getNextCursor());
        Long endRowNumber = CursoredUtil.calculateMaxRowNumber(startRowNumber, numberOfResults);

        booleanBuilder.and(user.id.goe(startRowNumber).and(user.id.lt(endRowNumber)));

        List<User> userList = new JPAQuery<List<FileSystemItemTopDownloadsView>>(entityManager)
                .select(user)
                .from(user)
                .where(booleanBuilder)
                .fetch();

        if (userList.isEmpty() ||
                userList.size() < numberOfResults ||
                startRowNumber > userList.get(userList.size() - 1).getId()) {
            endRowNumber = null;
        }

        return new PairDTO<>(userList, endRowNumber);
    }

}
