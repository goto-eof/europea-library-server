package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.FileSystemItemFullMapper;
import com.andreidodu.europealibrary.model.FeaturedFileMetaInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FeaturedFileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.FeaturedFileSystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FeaturedFileSystemItemServiceImpl extends CursoredServiceCommon implements FeaturedFileSystemItemService {
    private final FeaturedFileMetaInfoRepository featuredFileMetaInfoRepository;
    private final FileSystemItemFullMapper fileSystemItemFullMapper;
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public OperationStatusDTO addFeatured(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));
        FileMetaInfo fileMetaInfo = fileSystemItem.getFileMetaInfo();
        boolean alreadyAdded = fileMetaInfo.getFeaturedFileMetaInfo() != null;
        if (!alreadyAdded) {
            FeaturedFileMetaInfo featuredFileMetaInfo = new FeaturedFileMetaInfo();
            featuredFileMetaInfo.setFileMetaInfo(fileMetaInfo);
            this.featuredFileMetaInfoRepository.save(featuredFileMetaInfo);
            return new OperationStatusDTO(true, "Entity added");
        }
        return new OperationStatusDTO(true, "Entity already added");
    }

    @Override
    public OperationStatusDTO removeFeatured(Long fileSystemItemId) {
        FeaturedFileMetaInfo featuredFileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId)
                .map(fsi -> fsi.getFileMetaInfo().getFeaturedFileMetaInfo())
                .orElseThrow(() -> new EntityNotFoundException("File not found"));
        this.featuredFileMetaInfoRepository.delete(featuredFileSystemItem);
        return new OperationStatusDTO(true, "Entity removed");
    }

    @Override
    public OperationStatusDTO isFeatured(Long fileSystemItemId) {
        boolean isFeatured = this.fileSystemItemRepository.findById(fileSystemItemId)
                .stream().anyMatch(fsi -> fsi.getFileMetaInfo().getFeaturedFileMetaInfo() != null);
        return new OperationStatusDTO(isFeatured, isFeatured ? "yes" : "no");
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursored(CursorCommonRequestDTO cursorCommonRequestDTO) {
        return this.genericRetrieveCursored(cursorCommonRequestDTO, this.fileSystemItemFullMapper::toDTOWithParentDTORecursively);
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredHighlight(CursorCommonRequestDTO cursorCommonRequestDTO) {
        return this.genericRetrieveCursored(cursorCommonRequestDTO, this.fileSystemItemFullMapper::toHighlightDTO);
    }


    private <T> GenericCursoredResponseDTO<String, T> genericRetrieveCursored(CursorCommonRequestDTO cursorCommonRequestDTO, Function<FileSystemItem, T> toDTO) {
        GenericCursoredResponseDTO<String, T> responseDTO = new GenericCursoredResponseDTO<>();
        responseDTO.setParent("Featured");
        List<FileSystemItem> children = this.featuredFileMetaInfoRepository.retrieveCursored(cursorCommonRequestDTO);
        List<FileSystemItem> childrenList = limit(children, cursorCommonRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        responseDTO.setChildrenList(childrenList.stream()
                .map(toDTO)
                .collect(Collectors.toList()));
        super.calculateNextId(children, cursorCommonRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(responseDTO::setNextCursor);
        return responseDTO;
    }

}
