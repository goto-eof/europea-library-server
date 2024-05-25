package com.andreidodu.europealibrary.enums;

public enum OrderEnum {
    ASC("asc"), DESC("desc");

    private final String order;

    OrderEnum(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return this.order;
    }
}
