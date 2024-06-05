package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1/language")
public interface CursoredLanguageResource {
    @GetMapping
    ResponseEntity<List<ItemAndFrequencyDTO>> retrieveLanguages(Authentication authentication);

    @AllowOnlyAdministrator
    @PostMapping("/rename")
    ResponseEntity<OperationStatusDTO> bulkLanguageRename(Authentication authentication, @Valid @RequestBody RenameDTO renameDTO);
}
