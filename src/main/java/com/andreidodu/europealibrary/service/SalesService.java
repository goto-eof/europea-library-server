package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.PairDTO;

public interface SalesService {
    CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>> retrieveCursoredByTopSold(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
