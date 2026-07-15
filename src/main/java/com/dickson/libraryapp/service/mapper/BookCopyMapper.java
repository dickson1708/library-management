package com.dickson.libraryapp.service.mapper;

import com.dickson.libraryapp.domain.Book;
import com.dickson.libraryapp.domain.BookCopy;
import com.dickson.libraryapp.service.dto.BookCopyDTO;
import com.dickson.libraryapp.service.dto.BookDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookCopy} and its DTO {@link BookCopyDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookCopyMapper extends EntityMapper<BookCopyDTO, BookCopy> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    BookCopyDTO toDto(BookCopy s);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);
}
