package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.auth.AuthorityDTO;
import com.andreidodu.europealibrary.model.auth.Authority;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class RoleMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "user")
    public abstract Authority toModel(AuthorityDTO dto);


    public abstract AuthorityDTO toDTO(Authority model);

}
