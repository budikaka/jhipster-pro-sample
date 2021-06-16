package com.zbdigit.myapp.service.mapper;

import com.zbdigit.myapp.domain.Authority;
import com.zbdigit.myapp.service.dto.AuthoritySimpleDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthoritySimpleMapper extends EntityMapper<AuthoritySimpleDTO, Authority> {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "code", source = "code")
    AuthoritySimpleDTO toDto(Authority authority);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "code", source = "code")
    Authority toEntity(AuthoritySimpleDTO authoritySimpleDTO);
}
