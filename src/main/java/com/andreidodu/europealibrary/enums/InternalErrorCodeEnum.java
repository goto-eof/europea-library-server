package com.andreidodu.europealibrary.enums;

import lombok.Getter;

@Getter
public enum InternalErrorCodeEnum {
    MANDATORY_PAYEE_INFO("MANDATORY_PAYEE_INFO", "In order to proceed, first provide payee information");

    private final String internalErrorCode;
    private final String description;

    InternalErrorCodeEnum(String internalErrorCode, String description) {
        this.internalErrorCode = internalErrorCode;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.internalErrorCode;
    }

}