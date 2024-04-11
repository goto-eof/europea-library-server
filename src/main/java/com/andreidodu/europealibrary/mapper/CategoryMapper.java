package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class CategoryMapper {

    @Mapping(ignore = true, target = "bookInfoList")
    @Mapping(ignore = true, target = "id")
    public abstract Category toModel(CategoryDTO dto);

    public abstract CategoryDTO toDTO(Category model);

    public abstract List<CategoryDTO> toDTO(List<Category> modelList);
}
