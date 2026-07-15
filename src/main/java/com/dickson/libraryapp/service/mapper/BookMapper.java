package com.dickson.libraryapp.service.mapper;

import com.dickson.libraryapp.domain.Book;
import com.dickson.libraryapp.domain.Category;
import com.dickson.libraryapp.service.dto.BookDTO;
import com.dickson.libraryapp.service.dto.CategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryName")
    BookDTO toDto(Book s);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);
}
