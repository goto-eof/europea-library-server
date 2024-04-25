package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.annotation.auth.IsAdministrator;
import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.FileMetaInfoBookMapper;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.BookInfoService;
import com.andreidodu.europealibrary.service.CategoryService;
import com.andreidodu.europealibrary.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookInfoServiceImpl implements BookInfoService {
    private final FileMetaInfoRepository repository;
    private final FileMetaInfoBookMapper fileMetaInfoBookMapper;
    private final FileSystemItemRepository fileSystemItemRepository;
    private final TagService tagService;
    private final CategoryService categoryService;

    private static void validateUpdateInput(Long id, FileMetaInfoBookDTO dto) {
        if (id == null || !id.equals(dto.getId())) {
            throw new ApplicationException("IDs does not match");
        }
    }

    @Override
    public FileMetaInfoBookDTO retrieveById(Long id) {
        FileMetaInfo model = this.checkFileMetaInfoExistence(id);
        return this.fileMetaInfoBookMapper.toDTO(model);
    }

    @IsAdministrator
    @Override
    public FileMetaInfoBookDTO createBookInfo(FileMetaInfoBookDTO dto) {
        FileMetaInfo model = this.fileMetaInfoBookMapper.toModel(dto);
        model.setTagList(dto.getTagList()
                .stream()
                .map(tagDTO -> this.tagService.loadOrCreateTagEntity(tagDTO.getName()))
                .collect(Collectors.toList()));
        List<FileSystemItem> fileSystemItemList = this.fileSystemItemRepository.findAllById(dto.getFileSystemItemIdList());
        fileSystemItemList.forEach(fileSystemItem -> fileSystemItem.setFileMetaInfo(model));
        model.setFileSystemItemList(fileSystemItemList);

        FileMetaInfo newModel = this.repository.save(model);
        return this.fileMetaInfoBookMapper.toDTO(newModel);
    }

    @IsAdministrator
    @Override
    public FileMetaInfoBookDTO updateBookInfo(Long fileMetaInfoId, FileMetaInfoBookDTO dto) {
        validateUpdateInput(fileMetaInfoId, dto);
        FileMetaInfo model = checkFileMetaInfoExistence(fileMetaInfoId);
        this.fileMetaInfoBookMapper.map(model, dto);
        model.setTagList(dto.getTagList()
                .stream()
                .map(TagDTO::getName)
                .map(String::toLowerCase)
                .map(String::trim)
                .distinct()
                .map(this.tagService::loadOrCreateTagEntity)
                .collect(Collectors.toList()));
        model.getBookInfo().setCategoryList(dto.getCategoryList()
                .stream()
                .map(CategoryDTO::getName)
                .map(String::toLowerCase)
                .map(String::trim)
                .distinct()
                .map(this.categoryService::createCategoryEntity)
                .collect(Collectors.toList()));
        FileMetaInfo newModel = this.repository.save(model);
        return this.fileMetaInfoBookMapper.toDTO(newModel);
    }

    @IsAdministrator
    @Override
    public OperationStatusDTO delete(Long id) {
        return this.repository.findById(id).map(model -> {
                    this.fileSystemItemRepository.deleteAllInBatch(model.getFileSystemItemList());
                    this.repository.delete(model);
                    return new OperationStatusDTO(true, "deleted");
                })
                .orElse(new OperationStatusDTO(false, "not found"));
    }

    @Override
    public FileMetaInfoBookDTO retrieveByFileSystemItemId(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = checkFileSystemItemExistence(fileSystemItemId);
        return this.fileMetaInfoBookMapper.toDTO(fileSystemItem.getFileMetaInfo());
    }

    private FileSystemItem checkFileSystemItemExistence(Long fileSystemItemId) {
        return this.fileSystemItemRepository.findById(fileSystemItemId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    private FileMetaInfo checkFileMetaInfoExistence(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

}
