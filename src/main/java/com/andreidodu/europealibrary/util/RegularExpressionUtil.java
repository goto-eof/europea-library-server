package com.andreidodu.europealibrary.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RegularExpressionUtil {

    private final static String PATTERN_ISBN_STRING = "(?!(([a-z ]+)|(<[a-zA-Z=\" ]+>))((ISBN|isbn)[a-zA-Z0-9]*[:]?[\s]?))(?<=\s)([0-9]{13})(?![0-9]$)+(?=(</[a-zA-Z]*>))";
    private final static String PATTERN_SBN_STRING = "(?!(([a-z ]+)|(<[a-zA-Z=\" ]+>))((ISBN|(?!i)sbn)[a-zA-Z0-9]*[:]?[\s]?))(?<=\s)([0-9]{10})(?![0-9]$)+(?=(</[a-zA-Z]*>))";
    private static final Pattern ISBN_PATTERN_COMPILED = Pattern.compile(PATTERN_ISBN_STRING);
    private static final Pattern SBN_PATTERN_COMPILED = Pattern.compile(PATTERN_SBN_STRING);

    public Optional<String> extractISBN(String content) {
        return extractByPattern(content, ISBN_PATTERN_COMPILED);
    }

    public Optional<String> extractSBN(String content) {
        return extractByPattern(content, SBN_PATTERN_COMPILED);
    }

    public Optional<String> extractByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return Optional.ofNullable(matcher.group());
        }
        return Optional.empty();
    }


}
