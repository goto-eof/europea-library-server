package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.resource.CacheResource;
import com.andreidodu.europealibrary.service.CacheLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CacheResourceImpl implements CacheResource {
    final private CacheLoaderService cacheLoaderService;

    @Override
    public ResponseEntity<OperationStatusDTO> reloadCache(Authentication authentication) throws Exception {
        this.cacheLoaderService.reload(authentication);
        return ResponseEntity.ok(new OperationStatusDTO(true, "cache reloaded"));
    }
}
