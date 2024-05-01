package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.constants.RegexConst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RegularExpressionUtil {

    private final static String PATTERN_ISBN13_STRING = "(?!(([a-z ]+)|(<[a-zA-Z=\" ]+>))((ISBN|isbn)[a-zA-Z0-9]*[:]?[\s]?))(?<=\s)([0-9]{13})(?![0-9]$)+(?=(</[a-zA-Z]*>))";
    private final static String PATTERN_ISBN10_STRING = "(?!(([a-z ]+)|(<[a-zA-Z=\" ]+>))((ISBN|(?!i)sbn)[a-zA-Z0-9]*[:]?[\s]?))(?<=\s)([0-9]{10})(?![0-9]$)+(?=(</[a-zA-Z]*>))";
    private static final Pattern ISBN_PATTERN_COMPILED = Pattern.compile(PATTERN_ISBN13_STRING);
    private static final Pattern SBN_PATTERN_COMPILED = Pattern.compile(PATTERN_ISBN10_STRING);

    public Optional<String> extractISBN13(String content) {
        return extractByPattern(content, ISBN_PATTERN_COMPILED);
    }

    public Optional<String> extractISBN10(String content) {
        return extractByPattern(content, SBN_PATTERN_COMPILED);
    }

    public Optional<String> extractByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return Optional.ofNullable(matcher.group());
        }
        return Optional.empty();
    }

    public Optional<String> extractYear(String content) {
        return extractByPattern(content, RegexConst.PATTERN_COMPILED_YEAR);
    }

}
