package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.indexer.step.common.StepUtil;
import com.andreidodu.europealibrary.model.TmpAssociation;
import com.andreidodu.europealibrary.repository.TmpAssociationRepository;
import com.andreidodu.europealibrary.service.TmpAssociationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmpAssociationServiceImpl implements TmpAssociationService {
    private final TmpAssociationRepository tmpAssociationRepository;
    private final StepUtil stepUtil;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addItemsToTmpAssociationTable(Long targetId, List<String> items, int maxLength) {
        log.debug("add tags/categories to tmp table: {}", items);
        this.tmpAssociationRepository.saveAllAndFlush(
                this.stepUtil.explodeInUniqueItemsCleanedAndTrimmedToNullDistinctLowerCase(items, maxLength)
                        .stream()
                        .map(item -> buildTmpAssociation(targetId, item))
                        .toList());
        log.debug("records created successfully");
    }

    private TmpAssociation buildTmpAssociation(Long id, String value) {
        return new TmpAssociation(id, value);
    }
}
