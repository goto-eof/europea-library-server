package com.andreidodu.europealibrary.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TmpAssociationService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void addItemsToTmpAssociationTable(Long targetId, List<String> items);
}
