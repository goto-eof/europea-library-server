package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import lombok.Getter;

@Getter
public enum FileExtractionStatusEnum {
    SUCCESS(1), SUCCESS_EMPTY(2), FAILED(3);
    private final int status;

    FileExtractionStatusEnum(int status) {
        this.status = status;
    }

}
