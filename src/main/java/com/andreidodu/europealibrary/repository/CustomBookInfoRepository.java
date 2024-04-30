package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.Category;

import java.util.List;

public interface CustomBookInfoRepository {
    List<BookInfo> retrieveFileMetaInfoContainingCategory(Category category);

}
