package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.security.AuthUserDTO;
import com.andreidodu.europealibrary.model.security.User;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {AuthorityMapper.class})
public abstract class AuthUserMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "email")
    @Mapping(ignore = true, target = "resetToken")
    @Mapping(ignore = true, target = "stripeCustomer")
    @Mapping(ignore = true, target = "tokenList")
    @Mapping(ignore = true, target = "consensus1Flag")
    @Mapping(ignore = true, target = "consensus2Flag")
    @Mapping(ignore = true, target = "consensus3Flag")
    public abstract User toModel(AuthUserDTO dto);

    @Mapping(ignore = true, target = "authorities")
    public abstract AuthUserDTO toDTO(User model);

}
