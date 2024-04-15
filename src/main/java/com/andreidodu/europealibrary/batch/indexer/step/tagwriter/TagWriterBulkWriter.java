package com.andreidodu.europealibrary.batch.indexer.step.tagwriter;

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
public class TagWriterBulkWriter implements ItemWriter<String> {
    private static final String BULK_INSERT_TEMPLATE = "insert into el_tag( name , version ) values( ?, 1 );";
    private final DataSource dataSource;

    @Override
    public void write(Chunk<? extends String> chunk) throws SQLException {
        saveAll((List<String>) chunk.getItems());
    }

    private void saveAll(List<String> uniqueTagNameList) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(BULK_INSERT_TEMPLATE)) {

            for (String tagName : uniqueTagNameList) {
                preparedStatement.setString(1, tagName);
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.close();
        }
    }


}
