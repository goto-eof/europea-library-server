package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/category")
public interface CursoredCategoryResource {
    @PostMapping
    ResponseEntity<CursorDTO<CategoryDTO>> retrieveCategories(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO);

    @AllowOnlyAdministrator
    @PostMapping("/rename")
    ResponseEntity<OperationStatusDTO> bulkCategoryRename(@Valid @RequestBody RenameDTO renameDTO);
}
