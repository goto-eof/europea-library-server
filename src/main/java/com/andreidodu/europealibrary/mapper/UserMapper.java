package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.security.UserDTO;
import com.andreidodu.europealibrary.model.security.User;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {AuthorityMapper.class})
public abstract class UserMapper {

    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "enabled")
    @Mapping(ignore = true, target = "resetToken")
    public abstract User toModel(UserDTO dto);

    public abstract UserDTO toDTO(User model);

}
