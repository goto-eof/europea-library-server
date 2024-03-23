package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.strategy;

import lombok.Getter;

@Getter
public enum ApiStatusEnum {
    SUCCESS(1), SUCCESS_EMPTY_RESPONSE(2), FATAL_ERROR(3), ERROR(4);
    private final int code;

    ApiStatusEnum(int code) {
        this.code = code;
    }
}
