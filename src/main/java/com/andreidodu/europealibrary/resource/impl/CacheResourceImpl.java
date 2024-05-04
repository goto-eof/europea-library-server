package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.resource.CacheResource;
import com.andreidodu.europealibrary.service.CacheLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CacheResourceImpl implements CacheResource {
    final private CacheLoaderService cacheLoaderService;

    @Override
    public ResponseEntity<OperationStatusDTO> reloadCache() throws Exception {
        this.cacheLoaderService.reload();
        return ResponseEntity.ok(new OperationStatusDTO(true, "cache reloaded"));
    }
}
