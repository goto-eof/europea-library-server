package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.service.CacheLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostApplicationRunUtil {
    private final CacheLoader cacheLoader;

    public void performActions() {
        this.cacheLoader.reload();
    }

}
