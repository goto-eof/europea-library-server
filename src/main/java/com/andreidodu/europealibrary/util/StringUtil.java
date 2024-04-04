package com.andreidodu.europealibrary.util;

import org.springframework.stereotype.Component;

import java.util.*;
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

    public static String cleanOrTrimToNull(String str) {
        return Optional.ofNullable(str).map(string -> {
                    if (string.trim().isEmpty()) {
                        return null;
                    }
                    return clean(string);
                }
        ).orElse(null);
    }

    public static List<String> cleanOrTrimToNull(List<String> list) {
        return list.stream().filter(item -> cleanOrTrimToNull(item) != null).map(StringUtil::clean).collect(Collectors.toList());
    }

    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String substring(String str, int max_length) {
        if (str == null) {
            return null;
        }
        if (str.length() > max_length) {
            return str.substring(0, max_length);
        }
        return str;
    }

    public static List<String> splitString(String tagName) {
        final String trimmed = cleanOrTrimToNull(tagName);
        if (trimmed == null || trimmed.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        result.add(tagName);
        List<String> separators = List.of("/", "\\", ",", ";", " - ");
        for (String separator : separators) {
            result = splitArraysStrings(result, separator);
        }
        return new HashSet<>(result)
                .stream()
                .toList();
    }

    public static List<String> splitArraysStrings(List<String> array, String separator) {
        if (array == null || array.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String str : array) {
            str = cleanOrTrimToNull(str);
            if (str == null || str.isEmpty()) {
                continue;
            }
            if (str.contains(separator)) {
                result.addAll(splitStringByChar(str, separator));
                continue;
            }
            result.add(str);
        }

        return result;
    }

    private static List<String> splitStringByChar(String trimmed, String separator) {
        return Arrays.stream(trimmed.split(separator))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static String removeHTML(String str) {
        str = cleanOrTrimToNull(str);
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str.replaceAll("<[^>]*>", "");
    }
}
