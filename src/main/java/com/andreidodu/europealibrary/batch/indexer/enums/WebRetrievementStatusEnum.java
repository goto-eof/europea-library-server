package com.andreidodu.europealibrary.batch.indexer.enums;

import lombok.Getter;

@Getter
public enum WebRetrievementStatusEnum {
    SUCCESS(1), SUCCESS_EMPTY(2), FAILED(3);
    private final int status;

    WebRetrievementStatusEnum(int status) {
        this.status = status;
    }

}
