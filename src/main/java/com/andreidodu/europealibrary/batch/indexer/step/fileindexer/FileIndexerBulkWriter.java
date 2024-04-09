package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileIndexerBulkWriter implements ItemWriter<FileSystemItem> {
    private final JdbcWriter jdbcWriter;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) throws SQLException {
        jdbcWriter.bulkInsertOrUpdate((List<FileSystemItem>) chunk.getItems());
    }

}
