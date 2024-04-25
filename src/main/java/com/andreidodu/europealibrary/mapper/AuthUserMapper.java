package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.auth.AuthUserDTO;
import com.andreidodu.europealibrary.model.auth.User;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class AuthUserMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "email")
    @Mapping(ignore = true, target = "roleList")
    public abstract User toModel(AuthUserDTO dto);

    @Mapping(ignore = true, target = "authorities")
    public abstract AuthUserDTO toDTO(User model);

}
