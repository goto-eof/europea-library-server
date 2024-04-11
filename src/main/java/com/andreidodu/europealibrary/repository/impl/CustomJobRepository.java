package com.andreidodu.europealibrary.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * TODO this is a workaround, remove after tested well the feature
 */
@Repository
@RequiredArgsConstructor
public class CustomJobRepository {

    private final JdbcTemplate jdbcTemplate;
    String RESET_JOB_STATUS_QUERY_1 = "UPDATE BATCH_JOB_EXECUTION SET STATUS = 'FAILED', END_TIME = NOW() WHERE END_TIME IS NULL";
    String RESET_JOB_STATUS_QUERY_2 = "UPDATE BATCH_STEP_EXECUTION SET STATUS = 'FAILED', END_TIME = NOW() WHERE END_TIME IS NULL";

    public void resetJobs() {
        jdbcTemplate.execute(RESET_JOB_STATUS_QUERY_1);
        jdbcTemplate.execute(RESET_JOB_STATUS_QUERY_2);
    }

}
