package com.andreidodu.europealibrary.repository.util;

public interface CursoredUtil {

    static long calculateRowNumber(Long nextCursor) {
        return nextCursor == null ? 1 : nextCursor;
    }

    static long calculateMaxRowNumber(Long nextCursor, int numberOfResults) {
        return nextCursor == null ? numberOfResults : nextCursor + numberOfResults;
    }
}
