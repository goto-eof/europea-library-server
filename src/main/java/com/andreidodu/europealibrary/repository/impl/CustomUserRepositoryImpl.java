package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.model.FileSystemItemTopDownloadsView;
import com.andreidodu.europealibrary.model.security.QUser;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.CustomUserRepository;
import com.andreidodu.europealibrary.repository.util.CursoredUtil;
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


        Long nextCursor = commonCursoredRequestDTO.getNextCursor() != null ? commonCursoredRequestDTO.getNextCursor() : new JPAQuery<Long>(entityManager)
                .select(user.id.min())
                .from(user)
                .fetch().getFirst();

        Long startId = CursoredUtil.calculateRowNumber(nextCursor);
        booleanBuilder.and(user.id.goe(startId));

        List<User> userList = new JPAQuery<List<User>>(entityManager)
                .select(user)
                .from(user)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .fetch();

        if (userList.isEmpty() ||
                userList.size() < numberOfResults ||
                startId > userList.get(userList.size() - 1).getId()) {
            return new PairDTO<>(userList, null);
        }

        return new PairDTO<>(userList.stream().limit(numberOfResults).toList(), userList.get(userList.size() - 1).getId());
    }

}
