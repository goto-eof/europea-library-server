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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
