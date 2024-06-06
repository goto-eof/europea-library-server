package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/sales")
public interface SalesResource {

    @PostMapping(path = "/cursored/topSold")
    ResponseEntity<CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>>> retrieveCursoredByTopSold(Authentication authentication, @Valid @RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO);

}
