package com.andreidodu.europealibrary.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StringUtil {
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String clean(String str) {
        if (str != null) {
            return str.replaceAll("\u0000", "");
        }
        return null;
    }

    public static String cleanOrtrimToNull(String str) {
        return Optional.ofNullable(str).map(string -> {
                    if (string.trim().isEmpty()) {
                        return null;
                    }
                    return clean(string);
                }
        ).orElse(null);
    }

    public static List<String> cleanOrtrimToNull(List<String> list) {
        return list.stream().filter(item -> cleanOrtrimToNull(item) != null).map(StringUtil::clean).collect(Collectors.toList());
    }

    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }
}
