package com.dickson.libraryapp.service.mapper;

import static com.dickson.libraryapp.domain.BookAsserts.*;
import static com.dickson.libraryapp.domain.BookTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookMapperTest {

    private BookMapper bookMapper;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookSample1();
        var actual = bookMapper.toEntity(bookMapper.toDto(expected));
        assertBookAllPropertiesEquals(expected, actual);
    }
}
