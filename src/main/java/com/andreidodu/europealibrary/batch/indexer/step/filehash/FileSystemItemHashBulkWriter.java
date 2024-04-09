package com.andreidodu.europealibrary.batch.indexer.step.filehash;

import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileSystemItemHashBulkWriter implements ItemWriter<FileSystemItem> {
    private final DataSource dataSource;

    private static final String BULK_UPDATE_TEMPLATE = "update el_file_system_item set sha256=? where id=?;";

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) throws SQLException {
        if (chunk.getItems().isEmpty()) {
            return;
        }
        updateItems((List<FileSystemItem>) chunk.getItems());
    }

    private void updateItems(List<FileSystemItem> items) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(BULK_UPDATE_TEMPLATE)) {

            for (FileSystemItem item : items) {
                if (item.getSha256() != null) {
                    preparedStatement.setString(1, item.getSha256());
                } else {
                    preparedStatement.setNull(1, Types.NULL);
                }

                preparedStatement.setLong(2, item.getId());

                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.close();
        }
    }


}
