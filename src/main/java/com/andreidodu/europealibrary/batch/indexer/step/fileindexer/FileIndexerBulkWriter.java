package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileIndexerBulkWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final DataSource dataSource;

    private static final String BULK_INSERT_TEMPLATE = "insert into el_file_system_item (name, base_path, sha256, size, extension, file_create_date, file_update_date, is_directory, parent_id, job_step, file_meta_info_id, job_status, record_status, version) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) throws SQLException {
        List<FileSystemItem> items = chunk.getItems().stream().map(item -> (FileSystemItem) item).toList();
        if (items.isEmpty()) {
            return;
        }
        updateItems(chunk, items);
        bulkInsertItems(items);
    }

    private void bulkInsertItems(List<FileSystemItem> items) throws SQLException {
        List<FileSystemItem> inserts = items.stream().filter(item -> item.getId() == null).toList();
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(BULK_INSERT_TEMPLATE)) {
            for (FileSystemItem item : inserts) {
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
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    private void updateItems(Chunk<? extends FileSystemItem> chunk, List<FileSystemItem> items) {
        List<FileSystemItem> updates = items.stream().filter(item -> !Objects.isNull(item.getId())).toList();
        log.debug("Saving {} elements", chunk.getItems().size());
        this.fileSystemItemRepository.saveAll(updates);
        this.fileSystemItemRepository.flush();
    }

}
