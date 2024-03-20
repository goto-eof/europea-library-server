package com.andreidodu.europealibrary.util;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class StringUtil {
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

}
