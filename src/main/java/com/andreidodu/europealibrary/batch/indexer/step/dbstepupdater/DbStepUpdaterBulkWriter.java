package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
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
public class DbStepUpdaterBulkWriter implements ItemWriter<Long> {
    private final DataSource dataSource;

    private static final String BULK_UPDATE_TEMPLATE = "update el_file_system_item set job_step=?, record_status=? where id=?;";

    @Override
    public void write(Chunk<? extends Long> chunk) throws SQLException {
        updateItems((List<Long>) chunk.getItems());
    }

    private void updateItems(List<Long> ids) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(BULK_UPDATE_TEMPLATE)) {

            for (Long id : ids) {
                preparedStatement.setInt(1, JobStepEnum.READY.getStepNumber());
                preparedStatement.setInt(2, RecordStatusEnum.ENABLED.getStatus());

                preparedStatement.setLong(3, id);

                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.close();
        }
    }


}
