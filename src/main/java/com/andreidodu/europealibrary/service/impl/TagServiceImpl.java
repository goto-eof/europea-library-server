package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.mapper.TagMapper;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public CursorDTO<TagDTO> retrieveAllTags(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CursorDTO<TagDTO> cursoredResult = new CursorDTO<>();
        List<Tag> tagList = this.tagRepository.retrieveTagsCursored(commonCursoredRequestDTO);
        List<TagDTO> tagListDTO = this.tagMapper.toDTO(limit(tagList, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE));
        calculateNextId(tagList, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(cursoredResult::setNextCursor);
        cursoredResult.setItems(tagListDTO);
        return cursoredResult;
    }


}
