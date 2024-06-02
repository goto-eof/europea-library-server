package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.mapper.FileSystemItemLessMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemLessMapper fileSystemItemLessMapper;

    @Override
    public CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>> retrieveCursoredByTopSold(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>> cursoredResult = new CommonGenericCursoredResponseDTO<>();
        PairDTO<List<PairDTO<FileSystemItem, Long>>, Long> items = this.fileSystemItemRepository.retrieveCursoredByTopSold(commonCursoredRequestDTO);
        cursoredResult.setChildrenList(items.getVal1().stream()
                .map(pair -> new PairDTO<FileSystemItemDTO, Long>(fileSystemItemLessMapper.toDTOLess(pair.getVal1()), pair.getVal2()))
                .toList());
        cursoredResult.setNextCursor(items.getVal2());
        return cursoredResult;
    }
}
