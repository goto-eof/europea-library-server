package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

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
public class MetaInfoBulkWriter implements ItemWriter<FileSystemItem> {
    private final DataSource dataSource;

    private static final String BULK_UPDATE_TEMPLATE = "update el_file_system_item set file_meta_info_id=? where id=?;";

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) throws SQLException {
        updateItems((List<FileSystemItem>) chunk.getItems());
    }

    private void updateItems(List<FileSystemItem> items) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(BULK_UPDATE_TEMPLATE)) {

            for (FileSystemItem item : items) {
                if (item.getFileMetaInfoId() != null) {
                    preparedStatement.setLong(1, item.getFileMetaInfoId());
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
