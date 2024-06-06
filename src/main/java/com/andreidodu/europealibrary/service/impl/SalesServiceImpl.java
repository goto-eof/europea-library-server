package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.mapper.FileSystemItemLessMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SalesServiceImpl extends CursoredServiceCommon implements SalesService {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemLessMapper fileSystemItemLessMapper;

    @Override
    public CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>> retrieveCursoredByTopSold(Authentication authentication, CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>> cursoredResult = new CommonGenericCursoredResponseDTO<>();
        PaginatedExplorerOptions paginatedExplorerOptions = buildPaginatedExplorerOptions(authentication);
        PairDTO<List<PairDTO<FileSystemItem, Long>>, Long> items = this.fileSystemItemRepository.retrieveCursoredByTopSold(paginatedExplorerOptions, commonCursoredRequestDTO);
        cursoredResult.setChildrenList(items.getVal1().stream()
                .map(pair -> new PairDTO<FileSystemItemDTO, Long>(fileSystemItemLessMapper.toDTOLess(pair.getVal1()), pair.getVal2()))
                .toList());
        cursoredResult.setNextCursor(items.getVal2());
        return cursoredResult;
    }
}
