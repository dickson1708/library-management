package com.dickson.libraryapp.domain;

import static com.dickson.libraryapp.domain.LibraryMemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dickson.libraryapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LibraryMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LibraryMember.class);
        LibraryMember libraryMember1 = getLibraryMemberSample1();
        LibraryMember libraryMember2 = new LibraryMember();
        assertThat(libraryMember1).isNotEqualTo(libraryMember2);

        libraryMember2.setId(libraryMember1.getId());
        assertThat(libraryMember1).isEqualTo(libraryMember2);

        libraryMember2 = getLibraryMemberSample2();
        assertThat(libraryMember1).isNotEqualTo(libraryMember2);
    }
}
