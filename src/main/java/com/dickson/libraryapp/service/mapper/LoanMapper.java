package com.dickson.libraryapp.service.mapper;

import com.dickson.libraryapp.domain.BookCopy;
import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.domain.Loan;
import com.dickson.libraryapp.service.dto.BookCopyDTO;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.service.dto.LoanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Loan} and its DTO {@link LoanDTO}.
 */
@Mapper(componentModel = "spring")
public interface LoanMapper extends EntityMapper<LoanDTO, Loan> {
    @Mapping(target = "member", source = "member", qualifiedByName = "libraryMemberMembershipNumber")
    @Mapping(target = "bookCopy", source = "bookCopy", qualifiedByName = "bookCopyBarcode")
    LoanDTO toDto(Loan s);

    @Named("libraryMemberMembershipNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "membershipNumber", source = "membershipNumber")
    LibraryMemberDTO toDtoLibraryMemberMembershipNumber(LibraryMember libraryMember);

    @Named("bookCopyBarcode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "barcode", source = "barcode")
    BookCopyDTO toDtoBookCopyBarcode(BookCopy bookCopy);
}
