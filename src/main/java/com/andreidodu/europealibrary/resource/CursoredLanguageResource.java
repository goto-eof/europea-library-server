package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1/language")
public interface CursoredLanguageResource {
    @GetMapping
    ResponseEntity<List<ItemAndFrequencyDTO>> retrieveLanguages();

    @AllowOnlyAdministrator
    @PostMapping("/rename")
    ResponseEntity<OperationStatusDTO> bulkLanguageRename(@Valid @RequestBody RenameDTO renameDTO);
}
