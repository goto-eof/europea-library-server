package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.enums.OrderEnum;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.TagMapper;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.service.TagService;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.mysema.commons.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl extends CursoredServiceCommon implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final FileMetaInfoRepository fileMetaInfoRepository;

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_TAGS})
    public CursorDTO<TagDTO> retrieveAllTags(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        int limit = LimitUtil.calculateLimit(commonCursoredRequestDTO, ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE);

        CursorDTO<TagDTO> cursoredResult = new CursorDTO<>();
        List<Tag> tagList = this.tagRepository.retrieveTagsCursored(commonCursoredRequestDTO);
        List<TagDTO> tagListDTO = this.tagMapper.toDTO(limit(tagList, limit, ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE, OrderEnum.ASC));
        calculateNextId(tagList, limit, ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE, OrderEnum.ASC)
                .ifPresent(cursoredResult::setNextCursor);
        cursoredResult.setItems(tagListDTO);
        return cursoredResult;
    }

    private static Tag createTagFromName(String tag) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tag);
        return tagEntity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tag loadOrCreateTagEntity(String tagName) {
        Optional<Tag> tagOptional = this.tagRepository.findByNameIgnoreCase(tagName);
        Tag tag;
        if (tagOptional.isEmpty()) {
            tag = createTagFromName(tagName);
            tag = this.tagRepository.save(tag);
            this.tagRepository.flush();
        } else {
            tag = tagOptional.get();
        }
        return tag;
    }

    @Override
    public OperationStatusDTO bulkRename(RenameDTO renameDTO) {
        Assert.notNull(renameDTO, "null payload not allowed");
        Assert.notNull(renameDTO.getNewName(), "null newName not allowed");
        Assert.notNull(renameDTO.getOldName(), "null oldName not allowed");

        if (renameDTO.getNewName().equals(renameDTO.getOldName())) {
            return new OperationStatusDTO(true, "renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
        }

        Optional<Tag> oldTagOptional = this.tagRepository.findByNameIgnoreCase(renameDTO.getOldName().trim().toLowerCase());
        Optional<Tag> newTagOptional = this.tagRepository.findByNameIgnoreCase(renameDTO.getNewName().trim().toLowerCase());

        Tag oldTag = oldTagOptional.orElseThrow(() -> new ValidationException("Invalid old tag name"));
        Tag newTag = newTagOptional.orElseGet(() -> createTag(renameDTO.getNewName()));

        int count = this.updateTagReference(oldTag, newTag);
        return new OperationStatusDTO(count > 0, "renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
    }


    private int updateTagReference(Tag oldTag, Tag newTag) {
        int[] count = new int[1];

        this.fileMetaInfoRepository.retrieveFileMetaInfoContainingTag(oldTag)
                .forEach(fileMetaInfoEntity -> {
                    fileMetaInfoEntity.getTagList().remove(oldTag);
                    if (!fileMetaInfoEntity.getTagList().contains(newTag)) {
                        fileMetaInfoEntity.getTagList().add(newTag);
                    }
                    this.fileMetaInfoRepository.save(fileMetaInfoEntity);
                    count[0]++;
                });

        this.tagRepository.delete(oldTag);

        return count[0];
    }


    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name.trim().toLowerCase());
        return this.tagRepository.save(tag);
    }
}
