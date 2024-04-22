package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.mapper.TagMapper;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.service.TagService;
import com.andreidodu.europealibrary.util.LimitUtil;
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

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_TAGS})
    public CursorDTO<TagDTO> retrieveAllTags(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        int limit = LimitUtil.calculateLimit(commonCursoredRequestDTO, ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE);

        CursorDTO<TagDTO> cursoredResult = new CursorDTO<>();
        List<Tag> tagList = this.tagRepository.retrieveTagsCursored(commonCursoredRequestDTO);
        List<TagDTO> tagListDTO = this.tagMapper.toDTO(limit(tagList, limit));
        calculateNextId(tagList, limit)
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

}
