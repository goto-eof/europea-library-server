package com.andreidodu.europealibrary.batch.indexer.step.bookinfocategoryassociation;

import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookInfoCategoryAssociationBulkWriter implements ItemWriter<Pair<Long, Long>> {
    private static final String BULK_INSERT_TEMPLATE = "insert into el_book_info_category( book_info_id, category_id ) values( ?, ? );";
    private final DataSource dataSource;

    @Override
    public void write(Chunk<? extends Pair<Long, Long>> chunk) throws SQLException {
        saveAll((List<Pair<Long, Long>>) chunk.getItems());
    }

    private void saveAll(List<Pair<Long, Long>> fileMetaInfoIdTagIdList) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(BULK_INSERT_TEMPLATE)) {
            for (Pair<Long, Long> pair : fileMetaInfoIdTagIdList) {
                preparedStatement.setLong(1, pair.getFirst());
                preparedStatement.setLong(2, pair.getSecond());
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.close();
        }
    }


}
