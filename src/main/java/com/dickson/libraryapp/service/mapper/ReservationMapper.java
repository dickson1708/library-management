package com.dickson.libraryapp.service.mapper;

import com.dickson.libraryapp.domain.Book;
import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.domain.Reservation;
import com.dickson.libraryapp.service.dto.BookDTO;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.service.dto.ReservationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reservation} and its DTO {@link ReservationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    @Mapping(target = "member", source = "member", qualifiedByName = "libraryMemberMembershipNumber")
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    ReservationDTO toDto(Reservation s);

    @Named("libraryMemberMembershipNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "membershipNumber", source = "membershipNumber")
    LibraryMemberDTO toDtoLibraryMemberMembershipNumber(LibraryMember libraryMember);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);
}
