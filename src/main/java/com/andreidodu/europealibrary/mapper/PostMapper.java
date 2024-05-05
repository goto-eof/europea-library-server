package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.PostDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.Post;
import com.andreidodu.europealibrary.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class PostMapper {

    public abstract Post toModel(PostDTO dto);

    public abstract PostDTO toDTO(Post model);

    public abstract void map(PostDTO source, @MappingTarget Post target);

}
