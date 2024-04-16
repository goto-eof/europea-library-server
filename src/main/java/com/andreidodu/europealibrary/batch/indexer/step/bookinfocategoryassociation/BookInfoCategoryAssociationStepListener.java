package com.andreidodu.europealibrary.batch.indexer.step.bookinfocategoryassociation;

import com.andreidodu.europealibrary.repository.TmpAssociationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookInfoCategoryAssociationStepListener implements StepExecutionListener {
    private final TmpAssociationRepository tmpAssociationRepository;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        this.tmpAssociationRepository.deleteAllInBatch();
        return ExitStatus.COMPLETED;
    }
}
