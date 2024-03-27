package com.andreidodu.europealibrary.batch.indexer.enums;

import lombok.Getter;

@Getter
public enum RecordStatusEnum {
    JUST_UPDATED(1), ENABLED(2);
    private final int status;

    RecordStatusEnum(int status) {
        this.status = status;
    }

}
