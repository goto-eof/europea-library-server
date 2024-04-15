package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.batch.indexer.step.common.StepUtil;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.model.TmpAssociation;
import com.andreidodu.europealibrary.repository.TmpAssociationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TmpAssociationUtil {
    private final TmpAssociationRepository tmpAssociationRepository;
    private final StepUtil stepUtil;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addItemsToTmpAssociationTable(Long targetId, List<String> items) {
        this.tmpAssociationRepository.saveAllAndFlush(
                this.stepUtil.explodeInUniqueItemsCleanedAndTrimmedToNullDistinctLowerCase(items, DataPropertiesConst.TAG_NAME_MAX_LENGTH)
                        .stream()
                        .map(item -> buildTmpAssociation(targetId, item))
                        .toList());
    }

    private TmpAssociation buildTmpAssociation(Long id, String value) {
        return new TmpAssociation(id, value);
    }
}
