package com.dickson.libraryapp.service.mapper;

import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.domain.User;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LibraryMember} and its DTO {@link LibraryMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface LibraryMemberMapper extends EntityMapper<LibraryMemberDTO, LibraryMember> {
    @Mapping(target = "internalUser", source = "internalUser", qualifiedByName = "userLogin")
    LibraryMemberDTO toDto(LibraryMember s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
