package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/cache")
public interface CacheResource {
    @AllowOnlyAdministrator
    @GetMapping("/reload")
    ResponseEntity<OperationStatusDTO> reloadCache(Authentication authentication) throws Exception;
}
