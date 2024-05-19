package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.model.QBookInfo;
import com.andreidodu.europealibrary.repository.CustomBookInfoRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomBookInfoRepositoryImpl extends CommonRepository implements CustomBookInfoRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<BookInfo> retrieveFileMetaInfoContainingCategory(Category category) {
        QBookInfo bookInfo = QBookInfo.bookInfo;

        return new JPAQuery<BookInfo>(entityManager)
                .select(bookInfo)
                .from(bookInfo)
                .where(bookInfo.categoryList.contains(category))
                .fetch();
    }

}
