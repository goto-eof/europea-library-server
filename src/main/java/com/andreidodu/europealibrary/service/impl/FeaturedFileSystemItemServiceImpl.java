package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemHighlightDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FeaturedFileSystemItem;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FeaturedFileSystemRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.FeaturedFileSystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FeaturedFileSystemItemServiceImpl extends CursoredServiceCommon implements FeaturedFileSystemItemService {
    private final FeaturedFileSystemRepository featuredFileSystemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemDTO> retrieveCursored(CursorRequestDTO cursorRequestDTO) {
        GenericCursoredResponseDTO<String, FileSystemItemDTO> responseDTO = new GenericCursoredResponseDTO<>();
        responseDTO.setParent("Featured");
        List<FileSystemItem> children = this.featuredFileSystemRepository.retrieveCursored(cursorRequestDTO);
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        responseDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toDTOWithParentDTORecursively)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(responseDTO::setNextCursor);
        return responseDTO;
    }

    @Override
    public OperationStatusDTO addFeatured(Long fileSystemItemId) {
        Optional<FeaturedFileSystemItem> featuredFileSystemItemOptional = this.featuredFileSystemRepository.findByFileSystemItem_id(fileSystemItemId);
        if (featuredFileSystemItemOptional.isEmpty()) {
            Optional<FileSystemItem> fileSystemItemOptional = this.fileSystemItemRepository.findById(fileSystemItemId);
            if (fileSystemItemOptional.isEmpty()) {
                throw new EntityNotFoundException("File not found");
            }
            FeaturedFileSystemItem featuredFileSystemItem = new FeaturedFileSystemItem();
            featuredFileSystemItem.setFileSystemItem(fileSystemItemOptional.get());
            this.featuredFileSystemRepository.save(featuredFileSystemItem);
            return new OperationStatusDTO(true, "Entity added");
        }
        return new OperationStatusDTO(true, "Entity already added");
    }

    @Override
    public OperationStatusDTO removeFeatured(Long fileSystemItemId) {
        Optional<FeaturedFileSystemItem> featuredFileSystemItemOptional = this.featuredFileSystemRepository.findByFileSystemItem_id(fileSystemItemId);
        if (featuredFileSystemItemOptional.isEmpty()) {
            throw new EntityNotFoundException("File not found");
        }
        this.featuredFileSystemRepository.delete(featuredFileSystemItemOptional.get());
        return new OperationStatusDTO(true, "Entity removed");
    }

    @Override
    public OperationStatusDTO isFeatured(Long fileSystemItemId) {
        Optional<FeaturedFileSystemItem> featuredFileSystemItemOptional = this.featuredFileSystemRepository.findByFileSystemItem_id(fileSystemItemId);
        if (featuredFileSystemItemOptional.isPresent()) {
            return new OperationStatusDTO(true, "yes");
        }
        return new OperationStatusDTO(false, "no");
    }

    @Override
    public GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> retrieveCursoredHighlight(CursorRequestDTO cursorRequestDTO) {
        GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO> responseDTO = new GenericCursoredResponseDTO<>();
        responseDTO.setParent("FeaturedHighlight");
        List<FileSystemItem> children = this.featuredFileSystemRepository.retrieveCursored(cursorRequestDTO);
        List<FileSystemItem> childrenList = limit(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        responseDTO.setChildrenList(childrenList.stream()
                .map(this.fileSystemItemMapper::toHighlightDTO)
                .collect(Collectors.toList()));
        super.calculateNextId(children, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(responseDTO::setNextCursor);
        return responseDTO;
    }
}
