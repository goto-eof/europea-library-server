package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.strategy;

import lombok.Getter;

@Getter
public enum ApiStatusEnum {
    SUCCESS(1), FATAL_ERROR(2), ERROR(3);
    private final int code;

    ApiStatusEnum(int code) {
        this.code = code;
    }
}
