package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
// @Transactional(transactionManager = "jdbcTransactionManager")
public class JdbcWriter {

    private final DataSource dataSource;


    private static final String BULK_INSERT_TEMPLATE = "insert into el_file_system_item (name, base_path, sha256, size, extension, file_create_date, file_update_date, is_directory, parent_id, job_step, file_meta_info_id, job_status, record_status, version) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String BULK_UPDATE_TEMPLATE = "update el_file_system_item set name=?, base_path=?, sha256=?, size=?, extension=?, file_create_date=?, file_update_date=?, is_directory=?, parent_id=?, job_step=?, file_meta_info_id=?, job_status=?, record_status=?, version=? where id=?;";

    public void bulkInsertOrUpdate(List<FileSystemItem> fileSystemItemList) throws SQLException {
        if (fileSystemItemList.isEmpty()) {
            return;
        }
        List<FileSystemItem> inserts = fileSystemItemList.stream().filter(item -> item.getId() == null).toList();
        bulkInsertOrUpdate(inserts, true);
        List<FileSystemItem> updates = fileSystemItemList.stream().filter(item -> item.getId() != null).toList();
        bulkInsertOrUpdate(updates, false);
    }

    private void bulkInsertOrUpdate(List<FileSystemItem> items, boolean isInsert) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(isInsert ? BULK_INSERT_TEMPLATE : BULK_UPDATE_TEMPLATE)) {

            for (FileSystemItem item : items) {
                preparedStatement.setString(1, item.getName());
                preparedStatement.setString(2, item.getBasePath());
                preparedStatement.setString(3, item.getSha256());
                preparedStatement.setLong(4, item.getSize());
                preparedStatement.setString(5, item.getExtension());

                if (item.getFileCreateDate() != null) {
                    preparedStatement.setTimestamp(6, new java.sql.Timestamp(item.getFileCreateDate().toEpochSecond(ZoneOffset.UTC)));
                } else {
                    preparedStatement.setNull(6, java.sql.Types.NULL);
                }
                if (item.getFileUpdateDate() != null) {
                    preparedStatement.setTimestamp(7, new java.sql.Timestamp(item.getFileUpdateDate().toEpochSecond(ZoneOffset.UTC)));
                } else {
                    preparedStatement.setNull(7, java.sql.Types.NULL);
                }
                preparedStatement.setBoolean(8, item.getIsDirectory());

                if (item.getParent() != null) {
                    preparedStatement.setLong(9, item.getParentId());
                } else {
                    preparedStatement.setNull(9, java.sql.Types.NULL);
                }
                preparedStatement.setInt(10, item.getJobStep());

                if (item.getFileMetaInfoId() != null) {
                    preparedStatement.setLong(11, item.getFileMetaInfoId());
                } else {
                    preparedStatement.setNull(11, java.sql.Types.NULL);
                }

                if (item.getJobStatus() != null) {
                    preparedStatement.setInt(12, item.getJobStatus());
                } else {
                    preparedStatement.setNull(12, java.sql.Types.NULL);
                }

                if (item.getRecordStatus() != null) {
                    preparedStatement.setInt(13, item.getRecordStatus());
                } else {
                    preparedStatement.setNull(13, java.sql.Types.NULL);
                }
                preparedStatement.setInt(14, 0);

                if (!isInsert) {
                    preparedStatement.setLong(15, item.getId());
                }
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.close();
        }
    }
}
