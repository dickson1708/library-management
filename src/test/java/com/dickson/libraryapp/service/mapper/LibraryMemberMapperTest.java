package com.dickson.libraryapp.service.mapper;

import static com.dickson.libraryapp.domain.LibraryMemberAsserts.*;
import static com.dickson.libraryapp.domain.LibraryMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryMemberMapperTest {

    private LibraryMemberMapper libraryMemberMapper;

    @BeforeEach
    void setUp() {
        libraryMemberMapper = new LibraryMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLibraryMemberSample1();
        var actual = libraryMemberMapper.toEntity(libraryMemberMapper.toDto(expected));
        assertLibraryMemberAllPropertiesEquals(expected, actual);
    }
}
