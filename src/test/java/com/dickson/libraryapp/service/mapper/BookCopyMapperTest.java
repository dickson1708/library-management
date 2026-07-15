package com.dickson.libraryapp.service.mapper;

import static com.dickson.libraryapp.domain.BookCopyAsserts.*;
import static com.dickson.libraryapp.domain.BookCopyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookCopyMapperTest {

    private BookCopyMapper bookCopyMapper;

    @BeforeEach
    void setUp() {
        bookCopyMapper = new BookCopyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookCopySample1();
        var actual = bookCopyMapper.toEntity(bookCopyMapper.toDto(expected));
        assertBookCopyAllPropertiesEquals(expected, actual);
    }
}
