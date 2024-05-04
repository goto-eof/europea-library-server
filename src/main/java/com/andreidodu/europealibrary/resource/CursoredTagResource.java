package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/tag")
public interface CursoredTagResource {
    @PostMapping
    ResponseEntity<CursorDTO<TagDTO>> retrieveTagsCursored(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO);

    @AllowOnlyAdministrator
    @PostMapping("/rename")
    ResponseEntity<OperationStatusDTO> bulkTagRename(@Valid @RequestBody RenameDTO renameDTO);
}
